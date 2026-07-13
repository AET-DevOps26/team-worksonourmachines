import { aiApi } from '~/.server/api';
import { callApi } from '~/.server/service/apiCall';

export type {
    GeneratePlanResponse,
    PlanMilestone,
    PlanSuggestion,
    PlanTier,
    ProposedTutor,
} from '~/.server/api/server-ai';

export async function generatePlan(learningGoalId: string) {
    return callApi(() => aiApi.generatePlan(learningGoalId));
}
