import { marketplaceApi } from '~/.server/api';
import type {
    SharedMarketplaceAdminModuleInput,
    SharedMarketplaceAdminModuleUpdateInput,
} from '~/.server/api/server-marketplace/generated';
import { callApi } from '~/.server/service/apiCall';

export async function listModules(params?: { page?: number; pageSize?: number; q?: string }) {
    return callApi(() => marketplaceApi.listModules(params ?? {}));
}

export async function getModule(code: string) {
    return callApi(() => marketplaceApi.getModule({ code }));
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
