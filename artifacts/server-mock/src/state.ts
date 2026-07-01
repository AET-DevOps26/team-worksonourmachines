import { randomUUID } from 'node:crypto';

export const USER_IDS = {
  lukas: '11111111-1111-1111-1111-111111111101',
  anna: '11111111-1111-1111-1111-111111111102',
  max: '11111111-1111-1111-1111-111111111103',
  admin: '11111111-1111-1111-1111-111111111199',
} as const;

export type Weekday =
  | 'monday'
  | 'tuesday'
  | 'wednesday'
  | 'thursday'
  | 'friday'
  | 'saturday'
  | 'sunday';

export type Location = 'online' | 'garching' | 'munich' | 'weihenstephan' | 'staubing' | 'ottobrun';
export type ApplicationStatus = 'pending' | 'approved' | 'rejected';

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

export type TutorAvailability = { weekday: Weekday; available: boolean; note?: string };

export type TutorProfile = {
  id: string;
  userId: string;
  displayName: string;
  bio: string;
  languages: string[];
  locations: Location[];
  hourlyRate: number;
  availability: TutorAvailability[];
  published: boolean;
};

export type TutorCoverage = {
  moduleId: string;
  moduleCode: string;
  moduleTitle: string;
  proficiencyLevel: string;
};

export type TutorApplication = {
  id: string;
  userId: string;
  moduleId: string;
  moduleCode: string;
  moduleTitle: string;
  status: ApplicationStatus;
  certificateRef: string;
  submittedAt: string;
  rejectionReason?: string;
};

export type State = {
  studentProfiles: Map<string, StudentProfile>;
  tutorProfiles: Map<string, TutorProfile>;
  applications: TutorApplication[];
  coverages: Map<string, TutorCoverage[]>;
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
  const modules = createModules();
  const dwt = modules[0]!;
  const gdb = modules[1]!;

  const annaProfile: TutorProfile = {
    id: 'tutor-anna',
    userId: USER_IDS.anna,
    displayName: 'Anna Muller',
    bio: 'CS student with strong background in probability and foundations.',
    languages: ['German', 'English'],
    locations: ['garching', 'munich', 'online'],
    hourlyRate: 25,
    availability: [
      { weekday: 'monday', available: true, note: '18:00-21:00' },
      { weekday: 'wednesday', available: true, note: '16:00-20:00' },
      { weekday: 'friday', available: true, note: 'Flexible afternoons' },
    ],
    published: true,
  };

  const maxProfile: TutorProfile = {
    id: 'tutor-max',
    userId: USER_IDS.max,
    displayName: 'Max Hoffmann',
    bio: 'Tutor for discrete structures and linear algebra.',
    languages: ['German'],
    locations: ['online'],
    hourlyRate: 20,
    availability: [{ weekday: 'tuesday', available: true, note: '17:00-22:00' }],
    published: true,
  };

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

  const tutorProfiles = new Map<string, TutorProfile>([
    [USER_IDS.anna, annaProfile],
    [USER_IDS.max, maxProfile],
  ]);

  const coverages = new Map<string, TutorCoverage[]>([
    [
      USER_IDS.anna,
      [
        {
          moduleId: dwt.id,
          moduleCode: dwt.code,
          moduleTitle: dwt.title,
          proficiencyLevel: 'expert',
        },
        {
          moduleId: gdb.id,
          moduleCode: gdb.code,
          moduleTitle: gdb.title,
          proficiencyLevel: 'advanced',
        },
      ],
    ],
    [
      USER_IDS.max,
      [
        {
          moduleId: modules[2]!.id,
          moduleCode: modules[2]!.code,
          moduleTitle: modules[2]!.title,
          proficiencyLevel: 'advanced',
        },
      ],
    ],
  ]);

  const applications: TutorApplication[] = [
    {
      id: 'app-anna-dwt',
      userId: USER_IDS.anna,
      moduleId: dwt.id,
      moduleCode: dwt.code,
      moduleTitle: dwt.title,
      status: 'approved',
      certificateRef: 'cert://anna-dwt.pdf',
      submittedAt: '2025-09-01T10:00:00Z',
    },
    {
      id: 'app-anna-gdb',
      userId: USER_IDS.anna,
      moduleId: gdb.id,
      moduleCode: gdb.code,
      moduleTitle: gdb.title,
      status: 'approved',
      certificateRef: 'cert://anna-gdb.pdf',
      submittedAt: '2025-09-15T10:00:00Z',
    },
    {
      id: 'app-max-ds',
      userId: USER_IDS.max,
      moduleId: modules[2]!.id,
      moduleCode: modules[2]!.code,
      moduleTitle: modules[2]!.title,
      status: 'approved',
      certificateRef: 'cert://max-ds.pdf',
      submittedAt: '2025-10-01T10:00:00Z',
    },
  ];

  return {
    studentProfiles,
    tutorProfiles,
    applications,
    coverages,
    modules,
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

export function getModuleById(id: string) {
  return state.modules.find((m) => m.id === id);
}

export function getModuleByCode(code: string) {
  return state.modules.find((m) => m.code.toLowerCase() === code.toLowerCase());
}

export function getApprovedCoverages(userId: string): TutorCoverage[] {
  return state.coverages.get(userId) ?? [];
}

export function ratingForTutor(_userId: string) {
  return { average: 4.7, count: 12 };
}

export function tutorSummaryFromProfile(profile: TutorProfile): {
  id: string;
  userId: string;
  displayName: string;
  hourlyRate: number;
  languages: string[];
  locations: Location[];
  ratingSummary: { average: number; count: number };
  coverages: TutorCoverage[];
} {
  return {
    id: profile.id,
    userId: profile.userId,
    displayName: profile.displayName,
    hourlyRate: profile.hourlyRate,
    languages: profile.languages,
    locations: profile.locations,
    ratingSummary: ratingForTutor(profile.userId),
    coverages: getApprovedCoverages(profile.userId),
  };
}

export function listPublishedTutors() {
  return [...state.tutorProfiles.values()].filter((p) => p.published);
}

export function newId(prefix: string) {
  return `${prefix}-${randomUUID().slice(0, 8)}`;
}
