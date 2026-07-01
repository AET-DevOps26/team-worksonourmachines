import { Router } from 'express';
import { requireAuth } from '../auth.js';
import {
  getModuleByCode,
  newId,
  state,
  type StudyFocus,
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
