export const USER_IDS = {
  lukas: '11111111-1111-1111-1111-111111111101',
} as const;

export type StudyFocus = {
  memorization: number;
  formalReasoning: number;
  conceptualUnderstanding: number;
  problemSolving: number;
};

export type StudentProfile = {
  displayName: string;
  bio: string;
  languages: string[];
  studyFocus?: StudyFocus;
};

export type State = {
  studentProfiles: Map<string, StudentProfile>;
};

function createInitialState(): State {
  const studentProfiles = new Map<string, StudentProfile>([
    [
      USER_IDS.lukas,
      {
        displayName: 'Lukas Weber',
        bio: 'Switched majors, looking for DWT help.',
        languages: ['German', 'English'],
        studyFocus: {
          memorization: 3,
          formalReasoning: 4,
          conceptualUnderstanding: 3,
          problemSolving: 2,
        },
      },
    ],
  ]);

  return { studentProfiles };
}

export const state = createInitialState();

export function emptyProfile(): StudentProfile {
  return {
    bio: '',
    displayName: '',
    languages: [],
  };
}

const STUDY_FOCUS_KEYS = [
  'memorization',
  'formalReasoning',
  'conceptualUnderstanding',
  'problemSolving',
] as const;

export function parseStudyFocus(value: unknown): StudyFocus | undefined {
  if (!value || typeof value !== 'object') {
    return undefined;
  }
  const record = value as Record<string, unknown>;
  const focus: Partial<StudyFocus> = {};
  for (const key of STUDY_FOCUS_KEYS) {
    const raw = record[key];
    if (typeof raw !== 'number' || raw < 1 || raw > 5) {
      return undefined;
    }
    focus[key] = raw;
  }
  if (Object.keys(focus).length !== STUDY_FOCUS_KEYS.length) {
    return undefined;
  }
  return focus as StudyFocus;
}
