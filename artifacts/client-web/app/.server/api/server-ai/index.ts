import { env } from '~/.server/lib/env';
import { resolveAccessTokenForActiveRequest } from '~/.server/lib/requestAuth';
import { ErrorResponse } from '../error';

export type PlanTier = 'cheapest' | 'within_budget' | 'best_quality';

export interface ProposedTutor {
    displayName: string;
    hourlyRate: number;
    id: string;
}

export interface PlanMilestone {
    dueDate: string;
    estimatedCost: number;
    title: string;
    topicId: string;
    tutorId: string;
}

export interface PlanSuggestion {
    description: string;
    milestones: PlanMilestone[];
    proposedTutors: ProposedTutor[];
    tier: PlanTier;
    totalEstimatedCost: number;
}

export interface GeneratePlanResponse {
    learningGoalId: string;
    suggestions: PlanSuggestion[];
}

export const aiApi = {
    async generatePlan(learningGoalId: string): Promise<GeneratePlanResponse> {
        const token = await resolveAccessTokenForActiveRequest();
        const response = await fetch(`${env.get('SERVER_AI_API_URL')}/v1/plan`, {
            body: JSON.stringify({ learningGoalId: learningGoalId }),
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
            },
            method: 'POST',
        });

        if (response.status === 401) throw new ErrorResponse('unauthorized');
        if (response.status === 403) throw new ErrorResponse('forbidden');
        if (response.status === 404) throw new ErrorResponse('notFound');
        if (response.status === 400) throw new ErrorResponse('badRequest');
        if (response.status === 503) throw new ErrorResponse('serviceUnavailable');
        if (!response.ok) throw new ErrorResponse('internalServerError');

        return response.json() as Promise<GeneratePlanResponse>;
    },
};
