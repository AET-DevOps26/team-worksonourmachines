import type { SharedMarketplaceTopicInput } from '~/.server/api/server-marketplace/generated';
import { STUDY_FOCUS_FIELDS } from '~/components/profile';

type StudyFocus = SharedMarketplaceTopicInput['studyFocus'];

function parseStudyFocus(formData: FormData, prefix: string): StudyFocus | undefined {
    const focus: Partial<StudyFocus> = {};

    for (const field of STUDY_FOCUS_FIELDS) {
        const raw = formData.get(`${prefix}_${field.key}`);
        if (raw === null || raw === '') {
            return undefined;
        }
        const value = Number(raw);
        if (!Number.isInteger(value) || value < 1 || value > 5) {
            return undefined;
        }
        focus[field.key] = value;
    }

    return focus as StudyFocus;
}

function defaultStudyFocus(): StudyFocus {
    return {
        conceptualUnderstanding: 3,
        formalReasoning: 3,
        memorization: 3,
        problemSolving: 3,
    };
}

export function parseTopicsFromFormData(formData: FormData): SharedMarketplaceTopicInput[] {
    const count = Number(formData.get('topicCount') ?? 0);
    const topics: SharedMarketplaceTopicInput[] = [];

    for (let i = 0; i < count; i++) {
        const name = String(formData.get(`topic_${i}_name`) ?? '').trim();
        const description = String(formData.get(`topic_${i}_description`) ?? '').trim();
        const difficultyHint = String(formData.get(`topic_${i}_difficultyHint`) ?? '').trim();
        const studyFocus = parseStudyFocus(formData, `topic_${i}_studyFocus`) ?? defaultStudyFocus();

        if (!name) {
            continue;
        }

        topics.push({ description, difficultyHint, name, studyFocus });
    }

    return topics;
}

export type TopicDraft = {
    name: string;
    description: string;
    difficultyHint: string;
    studyFocus: StudyFocus;
};

export function emptyTopicDraft(): TopicDraft {
    return {
        description: '',
        difficultyHint: 'Medium',
        name: '',
        studyFocus: defaultStudyFocus(),
    };
}

export function topicToDraft(topic: {
    name: string;
    description: string;
    difficultyHint: string;
    studyFocus: StudyFocus;
}): TopicDraft {
    return {
        description: topic.description,
        difficultyHint: topic.difficultyHint,
        name: topic.name,
        studyFocus: topic.studyFocus,
    };
}
