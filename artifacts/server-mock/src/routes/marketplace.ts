import { Router } from 'express';
import { getReqUser, requireAuth } from '../auth.js';
import { assignTutorRole } from '../keycloakAdmin.js';
import {
  getApprovedCoverages,
  getModuleByCode,
  getModuleById,
  listPublishedTutors,
  newId,
  ratingForTutor,
  state,
  tutorSummaryFromProfile,
  type ApplicationStatus,
  type Location,
  type StudyFocus,
  type TutorApplication,
  type TutorCoverage,
  type TutorProfile,
  type Weekday,
} from '../state.js';

export const marketplaceRouter = Router();

marketplaceRouter.use(requireAuth);

function paginate<T>(items: T[], page: number, pageSize: number) {
  const start = (page - 1) * pageSize;
  return {
    items: items.slice(start, start + pageSize),
    page,
    pageSize,
    total: items.length,
  };
}

function defaultStudyFocus(): StudyFocus {
  return {
    memorization: 3,
    formalReasoning: 3,
    conceptualUnderstanding: 3,
    problemSolving: 3,
  };
}

marketplaceRouter.get('/modules', (req, res) => {
  const page = Number(req.query.page ?? 1);
  const pageSize = Number(req.query.pageSize ?? 20);
  const q = String(req.query.q ?? '').toLowerCase();
  let items = state.modules.map(({ topics: _t, ...m }) => m);
  if (q) {
    items = items.filter(
      (m) =>
        m.code.toLowerCase().includes(q) ||
        m.title.toLowerCase().includes(q) ||
        m.description.toLowerCase().includes(q),
    );
  }
  res.json(paginate(items, page, pageSize));
});

marketplaceRouter.get('/modules/:code', (req, res) => {
  const mod = getModuleByCode(req.params.code ?? '');
  if (!mod) {
    res.status(404).json({ code: 'not_found', message: 'Module not found' });
    return;
  }
  res.json(mod);
});

function tutorMatchesFilters(
  profile: TutorProfile,
  filters: {
    q?: string;
    moduleId?: string;
    topicId?: string;
    languages?: string[];
    locations?: Location[];
    minRate?: number;
    maxRate?: number;
    minRating?: number;
    weekdays?: Weekday[];
  },
) {
  if (!profile.published) return false;
  const coverages = getApprovedCoverages(profile.userId);
  if (filters.moduleId && !coverages.some((c) => c.moduleId === filters.moduleId)) return false;
  if (filters.topicId) {
    const mod = state.modules.find((m) => m.topics.some((t) => t.id === filters.topicId));
    if (!mod || !coverages.some((c) => c.moduleId === mod.id)) return false;
  }
  if (filters.languages?.length) {
    if (!filters.languages.every((l) => profile.languages.includes(l))) return false;
  }
  if (filters.locations?.length) {
    if (!filters.locations.some((l) => profile.locations.includes(l))) return false;
  }
  if (filters.minRate != null && profile.hourlyRate < filters.minRate) return false;
  if (filters.maxRate != null && profile.hourlyRate > filters.maxRate) return false;
  const rating = ratingForTutor(profile.userId);
  if (filters.minRating != null && rating.average < filters.minRating) return false;
  if (filters.weekdays?.length) {
    const availableOnAny = filters.weekdays.some((weekday) => {
      const day = profile.availability.find((a) => a.weekday === weekday);
      return day?.available;
    });
    if (!availableOnAny) return false;
  }
  if (filters.q) {
    const q = filters.q.toLowerCase();
    const hay = [profile.displayName, profile.bio, ...coverages.map((c) => c.moduleCode)].join(' ').toLowerCase();
    if (!hay.includes(q)) return false;
  }
  return true;
}

marketplaceRouter.get('/tutors', (req, res) => {
  const page = Number(req.query.page ?? 1);
  const pageSize = Number(req.query.pageSize ?? 20);
  const languages = req.query.languages
    ? Array.isArray(req.query.languages)
      ? (req.query.languages as string[])
      : [String(req.query.languages)]
    : undefined;
  const locations = req.query.locations
    ? Array.isArray(req.query.locations)
      ? (req.query.locations as Location[])
      : [String(req.query.locations) as Location]
    : undefined;
  const weekdays = req.query.weekdays
    ? Array.isArray(req.query.weekdays)
      ? (req.query.weekdays as Weekday[])
      : [String(req.query.weekdays) as Weekday]
    : undefined;

  const filters = {
    q: req.query.q ? String(req.query.q) : undefined,
    moduleId: req.query.moduleId ? String(req.query.moduleId) : undefined,
    topicId: req.query.topicId ? String(req.query.topicId) : undefined,
    languages,
    locations,
    minRate: req.query.minRate != null ? Number(req.query.minRate) : undefined,
    maxRate: req.query.maxRate != null ? Number(req.query.maxRate) : undefined,
    minRating: req.query.minRating != null ? Number(req.query.minRating) : undefined,
    weekdays,
  };

  let tutors = listPublishedTutors().filter((p) => tutorMatchesFilters(p, filters));
  const sort = req.query.sort ? String(req.query.sort) : 'rating';
  tutors = [...tutors].sort((a, b) => {
    if (sort === 'rate_asc') return a.hourlyRate - b.hourlyRate;
    if (sort === 'rate_desc') return b.hourlyRate - a.hourlyRate;
    if (sort === 'name') return a.displayName.localeCompare(b.displayName);
    return ratingForTutor(b.userId).average - ratingForTutor(a.userId).average;
  });

  const items = tutors.map((p) => tutorSummaryFromProfile(p));
  res.json(paginate(items, page, pageSize));
});

marketplaceRouter.get('/tutors/me', (req, res) => {
  const { sub } = getReqUser(req);
  const profile = state.tutorProfiles.get(sub) ?? null;
  const applications = state.applications.filter((a) => a.userId === sub);
  res.json({ profile, applications });
});

marketplaceRouter.put('/tutors/me', (req, res) => {
  const { sub } = getReqUser(req);
  const existing = state.tutorProfiles.get(sub);
  if (!existing) {
    res.status(400).json({ code: 'bad_request', message: 'Tutor profile does not exist' });
    return;
  }
  const body = req.body as Partial<TutorProfile>;
  const updated: TutorProfile = {
    ...existing,
    displayName: body.displayName?.trim() ?? existing.displayName,
    bio: body.bio?.trim() ?? existing.bio,
    languages: body.languages ?? existing.languages,
    locations: body.locations ?? existing.locations,
    hourlyRate: body.hourlyRate ?? existing.hourlyRate,
    availability: body.availability ?? existing.availability,
    published: body.published ?? existing.published,
  };
  state.tutorProfiles.set(sub, updated);
  res.json(updated);
});

marketplaceRouter.get('/tutors/:id', (req, res) => {
  const profile = [...state.tutorProfiles.values()].find((p) => p.id === req.params.id);
  if (!profile || !profile.published) {
    res.status(404).json({ code: 'not_found', message: 'Tutor not found' });
    return;
  }
  res.json({
    ...tutorSummaryFromProfile(profile),
    bio: profile.bio,
    availability: profile.availability,
    published: profile.published,
  });
});

marketplaceRouter.post('/tutor-applications', (req, res) => {
  const { sub } = getReqUser(req);
  const body = req.body as {
    moduleId?: string;
    certificateRef?: string;
    profile?: TutorProfile;
  };
  if (!body.moduleId || !body.certificateRef) {
    res.status(400).json({ code: 'bad_request', message: 'moduleId and certificateRef are required' });
    return;
  }
  const mod = getModuleById(body.moduleId);
  if (!mod) {
    res.status(400).json({ code: 'bad_request', message: 'Unknown module' });
    return;
  }
  const existingProfile = state.tutorProfiles.get(sub);
  if (!existingProfile) {
    if (!body.profile?.displayName) {
      res.status(400).json({ code: 'bad_request', message: 'profile is required on first application' });
      return;
    }
    const profile: TutorProfile = {
      id: newId('tutor'),
      userId: sub,
      displayName: body.profile.displayName,
      bio: body.profile.bio ?? '',
      languages: body.profile.languages ?? [],
      locations: body.profile.locations ?? [],
      hourlyRate: body.profile.hourlyRate ?? 0,
      availability: body.profile.availability ?? [],
      published: false,
    };
    state.tutorProfiles.set(sub, profile);
  }
  const pending = state.applications.find(
    (a) => a.userId === sub && a.moduleId === body.moduleId && a.status === 'pending',
  );
  if (pending) {
    res.status(400).json({ code: 'bad_request', message: 'Application already pending' });
    return;
  }
  const application: TutorApplication = {
    id: newId('app'),
    userId: sub,
    moduleId: mod.id,
    moduleCode: mod.code,
    moduleTitle: mod.title,
    status: 'pending',
    certificateRef: body.certificateRef,
    submittedAt: new Date().toISOString(),
  };
  state.applications.push(application);
  res.status(201).json(application);
});

marketplaceRouter.get('/tutor-applications/me', (req, res) => {
  const { sub } = getReqUser(req);
  res.json(state.applications.filter((a) => a.userId === sub));
});

marketplaceRouter.get('/admin/tutor-applications', (req, res) => {
  const status = req.query.status ? (String(req.query.status) as ApplicationStatus) : undefined;
  let apps = [...state.applications];
  if (status) apps = apps.filter((a) => a.status === status);
  res.json(apps);
});

marketplaceRouter.post('/admin/tutor-applications/:id/approve', async (req, res) => {
  const app = state.applications.find((a) => a.id === req.params.id);
  if (!app) {
    res.status(404).json({ code: 'not_found', message: 'Application not found' });
    return;
  }
  const priorApproved = state.applications.filter(
    (a) => a.userId === app.userId && a.status === 'approved',
  ).length;
  const isFirstApproval = priorApproved === 0;

  if (isFirstApproval) {
    try {
      await assignTutorRole(app.userId);
    } catch (error) {
      console.error('Failed to assign tutor role in Keycloak', { error, userId: app.userId });
      res.status(500).json({ code: 'internal_error', message: 'Failed to assign tutor role' });
      return;
    }
  }

  app.status = 'approved';
  const coverage: TutorCoverage = {
    moduleId: app.moduleId,
    moduleCode: app.moduleCode,
    moduleTitle: app.moduleTitle,
    proficiencyLevel: 'advanced',
  };
  const existing = state.coverages.get(app.userId) ?? [];
  if (!existing.some((c) => c.moduleId === app.moduleId)) {
    state.coverages.set(app.userId, [...existing, coverage]);
  }
  const profile = state.tutorProfiles.get(app.userId);
  if (profile) {
    profile.published = true;
  }
  res.json({
    application: app,
    isFirstApproval,
  });
});

marketplaceRouter.post('/admin/tutor-applications/:id/reject', (req, res) => {
  const app = state.applications.find((a) => a.id === req.params.id);
  if (!app) {
    res.status(404).json({ code: 'not_found', message: 'Application not found' });
    return;
  }
  app.status = 'rejected';
  app.rejectionReason = (req.body as { reason?: string }).reason;
  res.json(app);
});

marketplaceRouter.get('/admin/modules', (_req, res) => {
  res.json(state.modules);
});

marketplaceRouter.post('/admin/modules', (req, res) => {
  const body = req.body as {
    code?: string;
    title?: string;
    description?: string;
    difficultyHint?: string;
    topics?: { name: string; description: string; difficultyHint: string; studyFocus?: StudyFocus }[];
  };
  if (!body.code || !body.title) {
    res.status(400).json({ code: 'bad_request', message: 'code and title are required' });
    return;
  }
  if (getModuleByCode(body.code)) {
    res.status(400).json({ code: 'bad_request', message: 'Module code already exists' });
    return;
  }
  const mod = {
    id: newId('mod'),
    code: body.code.toUpperCase(),
    title: body.title,
    description: body.description ?? '',
    difficultyHint: body.difficultyHint ?? '',
    topics: (body.topics ?? []).map((t) => ({
      id: newId('topic'),
      name: t.name,
      description: t.description,
      difficultyHint: t.difficultyHint,
      studyFocus: t.studyFocus ?? defaultStudyFocus(),
    })),
  };
  state.modules.push(mod);
  res.json(mod);
});

marketplaceRouter.put('/admin/modules/:code', (req, res) => {
  const mod = getModuleByCode(req.params.code ?? '');
  if (!mod) {
    res.status(404).json({ code: 'not_found', message: 'Module not found' });
    return;
  }
  const body = req.body as {
    title?: string;
    description?: string;
    difficultyHint?: string;
    topics?: { name: string; description: string; difficultyHint: string; studyFocus?: StudyFocus }[];
  };
  mod.title = body.title ?? mod.title;
  mod.description = body.description ?? mod.description;
  mod.difficultyHint = body.difficultyHint ?? mod.difficultyHint;
  if (body.topics) {
    mod.topics = body.topics.map((t) => ({
      id: newId('topic'),
      name: t.name,
      description: t.description,
      difficultyHint: t.difficultyHint,
      studyFocus: t.studyFocus ?? defaultStudyFocus(),
    }));
  }
  res.json(mod);
});
