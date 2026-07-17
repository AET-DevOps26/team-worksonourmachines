import { useFetcher, useLoaderData } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { throwRouteError } from '~/.server/lib/routeError';
import {
    approveTutorApplication,
    listAdminTutorApplications,
    rejectTutorApplication,
} from '~/.server/service/marketplace';
import { roleProtectedAction, roleProtectedLoader } from '~/.server/service/routeProtection';
import { PageContainer } from '~/components/shell';
import { Badge } from '~/components/ui/badge';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';

export const loader = roleProtectedLoader('admin', async () => {
    const result = await listAdminTutorApplications('pending');
    if (isErr(result)) throwRouteError(result.error);
    return { applications: result.value };
});

export const action = roleProtectedAction('admin', async ({ request }) => {
    const formData = await request.formData();
    const intent = String(formData.get('intent') ?? '');
    const applicationId = String(formData.get('applicationId') ?? '');

    if (intent === 'approve') {
        const result = await approveTutorApplication(applicationId);
        if (isErr(result)) {
            return { error: 'Approval failed.' };
        }
        return { isFirstApproval: result.value.isFirstApproval, success: true };
    }

    if (intent === 'reject') {
        const reason = String(formData.get('reason') ?? '').trim();
        const result = await rejectTutorApplication(applicationId, reason ? { reason } : {});
        if (isErr(result)) {
            return { error: 'Rejection failed.' };
        }
        return { success: true };
    }

    return { error: 'Unknown action.' };
});

export default function AdminTutorApprovalsRoute() {
    const { applications } = useLoaderData<typeof loader>();
    const fetcher = useFetcher<typeof action>();
    const actionError = fetcher.data && 'error' in fetcher.data ? fetcher.data.error : null;

    return (
        <PageContainer className="flex flex-col gap-6">
            <Card>
                <CardTitle>Tutor approvals</CardTitle>
                <CardDescription>Review pending tutor applications and certificates.</CardDescription>
            </Card>
            {actionError ? (
                <div className="rounded-md border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                    {actionError}
                </div>
            ) : null}
            {applications.length === 0 ? (
                <Card>
                    <CardDescription>No pending applications.</CardDescription>
                </Card>
            ) : (
                applications.map((app) => (
                    <Card key={app.id}>
                        <div className="flex items-center justify-between gap-2">
                            <CardTitle className="text-base">
                                {app.moduleCode} — {app.moduleTitle}
                            </CardTitle>
                            <Badge variant="warning">{app.status}</Badge>
                        </div>
                        <CardDescription className="mt-2">Certificate: {app.certificateRef}</CardDescription>
                        <CardDescription suppressHydrationWarning>
                            Submitted {new Date(app.submittedAt).toLocaleDateString()}
                        </CardDescription>
                        <div className="mt-4 flex flex-wrap gap-2">
                            <fetcher.Form method="post">
                                <input name="applicationId" type="hidden" value={app.id} />
                                <input name="intent" type="hidden" value="approve" />
                                <Button disabled={fetcher.state !== 'idle'} size="sm" type="submit">
                                    Approve
                                </Button>
                            </fetcher.Form>
                            <fetcher.Form className="flex gap-2" method="post">
                                <input name="applicationId" type="hidden" value={app.id} />
                                <input name="intent" type="hidden" value="reject" />
                                <input
                                    className="h-8 rounded-md border border-input px-2 text-sm"
                                    name="reason"
                                    placeholder="Rejection reason"
                                />
                                <Button disabled={fetcher.state !== 'idle'} size="sm" type="submit" variant="outline">
                                    Reject
                                </Button>
                            </fetcher.Form>
                        </div>
                    </Card>
                ))
            )}
        </PageContainer>
    );
}
