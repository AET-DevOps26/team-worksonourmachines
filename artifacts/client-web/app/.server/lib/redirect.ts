// Reject protocol-relative and off-site paths to prevent open redirects.
export function safeRedirectPath(path: string | null): string {
    if (!path?.startsWith('/') || path.startsWith('//')) {
        return '/';
    }

    return path;
}

export function toExternalUrl(request: Request): URL {
    const url = new URL(request.url);
    if (url.hostname !== 'localhost' && url.protocol === 'http:') {
        url.protocol = 'https:';
    }

    return url;
}
