import { isRouteErrorResponse } from 'react-router';

const safeErrorCodes = new Set([
    'badRequest',
    'forbidden',
    'internalServerError',
    'notFound',
    'serviceUnavailable',
    'unauthorized',
    'unknown',
]);

type SafeRouteErrorPayload = {
    code: string;
    message: string;
};

type RouteErrorPresentation = {
    description: string;
    linkLabel: string;
    linkTo: string;
    signInAgain: boolean;
    retry: boolean;
    title: string;
};

export function getRouteErrorPresentation(error: unknown): RouteErrorPresentation {
    const status = isRouteErrorResponse(error) ? error.status : 500;
    const errorData = isRouteErrorResponse(error) ? error.data : undefined;
    return getStatusErrorPresentation(status, errorData);
}

function getStatusErrorPresentation(status: number, errorData?: unknown): RouteErrorPresentation {
    const safeMessage = getSafeMessage(errorData);
    if (status === 401) {
        return {
            description: safeMessage ?? 'Your session has expired. Please sign in again.',
            linkLabel: 'Go to sign in',
            linkTo: '/login',
            retry: false,
            signInAgain: true,
            title: 'Sign in required',
        };
    }

    if (status === 403) {
        return {
            description: safeMessage ?? 'You do not have permission to view this page.',
            linkLabel: 'Go to dashboard',
            linkTo: '/dashboard',
            retry: false,
            signInAgain: false,
            title: 'Access denied',
        };
    }

    if (status === 404) {
        return {
            description: safeMessage ?? 'We could not find what you were looking for.',
            linkLabel: 'Go to dashboard',
            linkTo: '/dashboard',
            retry: false,
            signInAgain: false,
            title: 'Page not found',
        };
    }

    if (status === 503) {
        return {
            description: safeMessage ?? 'This service is temporarily unavailable. Please try again.',
            linkLabel: 'Go to dashboard',
            linkTo: '/dashboard',
            retry: true,
            signInAgain: false,
            title: 'Temporarily unavailable',
        };
    }

    if (status === 400) {
        return {
            description: safeMessage ?? 'The request could not be completed.',
            linkLabel: 'Go to dashboard',
            linkTo: '/dashboard',
            retry: false,
            signInAgain: false,
            title: 'Request could not be completed',
        };
    }

    return {
        description: safeMessage ?? 'Something went wrong. Please try again.',
        linkLabel: 'Go to dashboard',
        linkTo: '/dashboard',
        retry: true,
        signInAgain: false,
        title: 'Something went wrong',
    };
}

function getSafeMessage(data: unknown): string | undefined {
    if (typeof data !== 'object' || data === null) return undefined;
    const payload = data as Partial<SafeRouteErrorPayload>;
    if (typeof payload.code !== 'string' || !safeErrorCodes.has(payload.code)) return undefined;
    return typeof payload.message === 'string' ? payload.message : undefined;
}
