import { studentApi } from '~/.server/api';
import type { SharedStudentStudentProfileInput } from '~/.server/api/server-student/generated';
import { callApi } from '~/.server/service/apiCall';

export async function getStudentProfile() {
    return callApi(() => studentApi.getMyProfile());
}

export async function updateStudentProfile(input: SharedStudentStudentProfileInput) {
    return callApi(() => studentApi.updateMyProfile({ sharedStudentStudentProfileInput: input }));
}
