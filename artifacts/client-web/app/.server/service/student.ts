import { studentApi } from '~/.server/api';
import type {
    SharedStudentLearningGoalInput,
    SharedStudentStudentProfileInput,
} from '~/.server/api/server-student/generated';
import { callApi } from '~/.server/service/apiCall';

export async function getStudentProfile() {
    return callApi(() => studentApi.getMyProfile());
}

export async function updateStudentProfile(input: SharedStudentStudentProfileInput) {
    return callApi(() => studentApi.updateMyProfile({ sharedStudentStudentProfileInput: input }));
}

export async function listMyGoals() {
    return callApi(() => studentApi.listMyGoals());
}

export async function getGoal(id: string) {
    return callApi(() => studentApi.getGoal({ id }));
}

export async function createGoal(input: SharedStudentLearningGoalInput) {
    return callApi(() => studentApi.createGoal({ sharedStudentLearningGoalInput: input }));
}

export async function updateGoal(id: string, input: SharedStudentLearningGoalInput) {
    return callApi(() => studentApi.updateGoal({ id, sharedStudentLearningGoalInput: input }));
}

export async function deleteGoal(id: string) {
    return callApi(() => studentApi.deleteGoal({ id }));
}
