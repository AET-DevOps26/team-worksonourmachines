import { marketplaceApi } from '~/.server/api';
import type {
    SharedMarketplaceAdminModuleInput,
    SharedMarketplaceAdminModuleUpdateInput,
    SharedMarketplaceApplicationStatus,
    SharedMarketplaceLocation,
    SharedMarketplaceRejectApplicationRequest,
    SharedMarketplaceSubmitTutorApplicationRequest,
    SharedMarketplaceTutorProfileInput,
    SharedMarketplaceTutorSort,
    SharedMarketplaceWeekday,
} from '~/.server/api/server-marketplace/generated';
import { callApi } from '~/.server/service/apiCall';

export async function listModules(params?: { page?: number; pageSize?: number; q?: string }) {
    return callApi(() => marketplaceApi.listModules(params ?? {}));
}

export async function getModule(code: string) {
    return callApi(() => marketplaceApi.getModule({ code }));
}

export async function listTutors(params?: {
    page?: number;
    pageSize?: number;
    q?: string;
    moduleId?: string;
    topicId?: string;
    languages?: string[];
    locations?: SharedMarketplaceLocation[];
    minRate?: number;
    maxRate?: number;
    minRating?: number;
    weekdays?: SharedMarketplaceWeekday[];
    sort?: SharedMarketplaceTutorSort;
}) {
    return callApi(() => marketplaceApi.listTutors(params ?? {}));
}

export async function getTutor(id: string) {
    return callApi(() => marketplaceApi.getTutor({ id }));
}

export async function getMyTutorProfile() {
    return callApi(() => marketplaceApi.getMyTutorProfile());
}

export async function updateMyTutorProfile(input: SharedMarketplaceTutorProfileInput) {
    return callApi(() => marketplaceApi.updateMyTutorProfile({ sharedMarketplaceTutorProfileInput: input }));
}

export async function submitTutorApplication(input: SharedMarketplaceSubmitTutorApplicationRequest) {
    return callApi(() =>
        marketplaceApi.submitTutorApplication({ sharedMarketplaceSubmitTutorApplicationRequest: input }),
    );
}

export async function listMyTutorApplications() {
    return callApi(() => marketplaceApi.listMyTutorApplications());
}

export async function listAdminTutorApplications(status?: SharedMarketplaceApplicationStatus) {
    const request = status ? { status } : {};
    return callApi(() => marketplaceApi.listAdminTutorApplications(request));
}

export async function approveTutorApplication(id: string) {
    return callApi(() => marketplaceApi.approveTutorApplication({ id }));
}

export async function rejectTutorApplication(id: string, body: SharedMarketplaceRejectApplicationRequest) {
    return callApi(() =>
        marketplaceApi.rejectTutorApplication({ id, sharedMarketplaceRejectApplicationRequest: body }),
    );
}

export async function listAdminModules() {
    return callApi(() => marketplaceApi.listAdminModules());
}

export async function createAdminModule(input: SharedMarketplaceAdminModuleInput) {
    return callApi(() => marketplaceApi.createAdminModule({ sharedMarketplaceAdminModuleInput: input }));
}

export async function updateAdminModule(code: string, input: SharedMarketplaceAdminModuleUpdateInput) {
    return callApi(() => marketplaceApi.updateAdminModule({ code, sharedMarketplaceAdminModuleUpdateInput: input }));
}
