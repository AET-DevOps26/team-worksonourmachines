import { randomUUID } from 'node:crypto';

export const USER_IDS = {
  lukas: '11111111-1111-1111-1111-111111111101',
} as const;

export type StudyFocus = {
  memorization: number;
  formalReasoning: number;
  conceptualUnderstanding: number;
  problemSolving: number;
};

export type Topic = {
  id: string;
  name: string;
  description: string;
  difficultyHint: string;
  studyFocus: StudyFocus;
};

export type ModuleDetail = {
  id: string;
  code: string;
  title: string;
  description: string;
  difficultyHint: string;
  topics: Topic[];
};

export type StudentProfile = {
  displayName: string;
  bio: string;
  languages: string[];
  studyFocus?: StudyFocus;
};

export type State = {
  studentProfiles: Map<string, StudentProfile>;
  modules: ModuleDetail[];
};

function defaultStudyFocus(): StudyFocus {
  return {
    memorization: 3,
    formalReasoning: 3,
    conceptualUnderstanding: 3,
    problemSolving: 3,
  };
}

function topic(
  id: string,
  name: string,
  description: string,
  difficultyHint: string,
  studyFocus: StudyFocus = defaultStudyFocus(),
): Topic {
  return { id, name, description, difficultyHint, studyFocus };
}

function createModules(): ModuleDetail[] {
  return [
    {
      id: 'mod-dwt',
      code: 'DWT',
      title: 'Diskrete Wahrscheinlichkeitstheorie',
      description: 'Probability theory for computer science students.',
      difficultyHint: 'Medium',
      topics: [
        topic('dwt-t1', 'Probability spaces', 'Sigma algebras and measures.', 'Hard', {
          memorization: 2,
          formalReasoning: 5,
          conceptualUnderstanding: 4,
          problemSolving: 4,
        }),
        topic('dwt-t2', 'Random variables', 'Expectation and variance.', 'Medium', {
          memorization: 3,
          formalReasoning: 4,
          conceptualUnderstanding: 4,
          problemSolving: 3,
        }),
        topic('dwt-t3', 'Limit theorems', 'LLN and CLT.', 'Hard', {
          memorization: 2,
          formalReasoning: 5,
          conceptualUnderstanding: 5,
          problemSolving: 4,
        }),
      ],
    },
    {
      id: 'mod-gdb',
      code: 'GDB',
      title: 'Grundlagen der Informatik',
      description: 'Foundational computer science concepts.',
      difficultyHint: 'Easy',
      topics: [
        topic('gdb-t1', 'Logic', 'Propositional and predicate logic.', 'Easy'),
        topic('gdb-t2', 'Automata', 'Finite automata and regular languages.', 'Medium'),
      ],
    },
    {
      id: 'mod-ds',
      code: 'DS',
      title: 'Diskrete Strukturen',
      description: 'Discrete structures for CS.',
      difficultyHint: 'Hard',
      topics: [
        topic('ds-t1', 'Graphs', 'Basic graph theory.', 'Medium'),
        topic('ds-t2', 'Combinatorics', 'Counting principles.', 'Hard'),
      ],
    },
    {
      id: 'mod-la',
      code: 'LA',
      title: 'Lineare Algebra',
      description: 'Linear algebra for engineers.',
      difficultyHint: 'Medium',
      topics: [
        topic('la-t1', 'Vector spaces', 'Bases and dimension.', 'Medium'),
        topic('la-t2', 'Eigenvalues', 'Diagonalization.', 'Hard'),
      ],
    },
  ];
}

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

  return {
    studentProfiles,
    modules: createModules(),
  };
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

export function getModuleByCode(code: string) {
  return state.modules.find((m) => m.code.toLowerCase() === code.toLowerCase());
}

export function newId(prefix: string) {
  return `${prefix}-${randomUUID().slice(0, 8)}`;
}
