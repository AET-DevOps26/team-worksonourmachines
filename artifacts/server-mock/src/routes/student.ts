import { Router } from 'express';
import { getReqUser, requireAuth } from '../auth.js';
import { emptyProfile, parseStudyFocus, state } from '../state.js';

export const studentRouter = Router();

studentRouter.use(requireAuth);

studentRouter.get('/students/me', (req, res) => {
  const { sub } = getReqUser(req);
  const profile = state.studentProfiles.get(sub) ?? emptyProfile();
  res.json(profile);
});

studentRouter.put('/students/me', (req, res) => {
  const { sub } = getReqUser(req);
  const body = req.body as {
    displayName?: string;
    bio?: string;
    languages?: string[];
    studyFocus?: unknown;
  };
  if (!body.displayName?.trim()) {
    res.status(400).json({ code: 'bad_request', message: 'displayName is required' });
    return;
  }

  const studyFocus = body.studyFocus === undefined ? undefined : parseStudyFocus(body.studyFocus);
  if (body.studyFocus !== undefined && studyFocus === undefined) {
    res.status(400).json({ code: 'bad_request', message: 'studyFocus values must be integers from 1 to 5' });
    return;
  }

  const profile = {
    displayName: body.displayName.trim(),
    bio: body.bio?.trim() ?? '',
    languages: Array.isArray(body.languages) ? body.languages : [],
    ...(studyFocus ? { studyFocus } : {}),
  };
  state.studentProfiles.set(sub, profile);
  res.json(profile);
});
