import { useId } from 'react';
import { Form, Link, redirect, useActionData, useLoaderData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { getModule, updateAdminModule } from '~/.server/service/marketplace';
import { roleProtectedAction, roleProtectedLoader } from '~/.server/service/routeProtection';
import { ModuleTopicEditor, parseTopicsFromFormData, topicToDraft } from '~/components/module';
import { Badge } from '~/components/ui/badge';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Textarea } from '~/components/ui/textarea';
import { cn } from '~/lib/ui/utils';

const adminModuleFormPageClassName = 'mx-auto flex w-full max-w-3xl flex-col gap-6';

export const loader = roleProtectedLoader('admin', async ({ params }) => {
    const code = params.code ?? '';
    const result = await getModule(code);
    if (isErr(result)) {
        throw result.error;
    }
    return { module: result.value };
});

export const action = roleProtectedAction('admin', async ({ request, params }) => {
    const code = params.code ?? '';
    const formData = await request.formData();
    const title = String(formData.get('title') ?? '').trim();

    if (!title) {
        return { error: 'Title is required.' };
    }

    const topics = parseTopicsFromFormData(formData);

    const result = await updateAdminModule(code, {
        description: String(formData.get('description') ?? '').trim(),
        difficultyHint: String(formData.get('difficultyHint') ?? '').trim(),
        title,
        topics,
    });

    if (isErr(result)) {
        return { error: 'Could not update module.' };
    }

    return redirect('/admin/modules');
});

export default function AdminEditModuleRoute() {
    const { module } = useLoaderData<typeof loader>();
    const actionData = useActionData() as { error?: string } | undefined;
    const navigation = useNavigation();
    const isSubmitting = navigation.state === 'submitting';
    const titleId = useId();
    const descriptionId = useId();
    const difficultyId = useId();

    return (
        <div className={adminModuleFormPageClassName}>
            <Card>
                <div className="flex flex-wrap items-center gap-2">
                    <CardTitle>Edit {module.code}</CardTitle>
                    <Badge variant="outline">{module.difficultyHint}</Badge>
                </div>
                <CardDescription className="mt-2">Update module details and manage topics.</CardDescription>
            </Card>

            <Card>
                <Form className="flex flex-col gap-6" method="post">
                    <div className="grid gap-4">
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={titleId}>Title</Label>
                            <Input defaultValue={module.title} id={titleId} name="title" required />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={descriptionId}>Description</Label>
                            <Textarea defaultValue={module.description} id={descriptionId} name="description" />
                        </div>
                        <div className="flex flex-col gap-2 sm:max-w-xs">
                            <Label htmlFor={difficultyId}>Difficulty</Label>
                            <Input
                                defaultValue={module.difficultyHint}
                                id={difficultyId}
                                name="difficultyHint"
                                placeholder="Easy / Medium / Hard"
                            />
                        </div>
                    </div>

                    <ModuleTopicEditor initialTopics={module.topics.map(topicToDraft)} />

                    {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}

                    <div className="flex flex-wrap gap-2">
                        <Button disabled={isSubmitting} type="submit">
                            {isSubmitting ? 'Saving…' : 'Save changes'}
                        </Button>
                        <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/admin/modules">
                            Cancel
                        </Link>
                    </div>
                </Form>
            </Card>
        </div>
    );
}
