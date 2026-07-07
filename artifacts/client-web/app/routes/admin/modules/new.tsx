import { useId } from 'react';
import { Form, Link, redirect, useActionData, useNavigation } from 'react-router';
import { isErr } from '~/.server/lib/result';
import { createAdminModule } from '~/.server/service/marketplace';
import { roleProtectedAction } from '~/.server/service/routeProtection';
import { ModuleTopicEditor, parseTopicsFromFormData } from '~/components/module';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Textarea } from '~/components/ui/textarea';
import { cn } from '~/lib/ui/utils';

const adminModuleFormPageClassName = 'mx-auto flex w-full max-w-3xl flex-col gap-6';

export const action = roleProtectedAction('admin', async ({ request }) => {
    const formData = await request.formData();
    const code = String(formData.get('code') ?? '').trim();
    const title = String(formData.get('title') ?? '').trim();

    if (!code || !title) {
        return { error: 'Code and title are required.' };
    }

    const topics = parseTopicsFromFormData(formData);

    const result = await createAdminModule({
        code,
        description: String(formData.get('description') ?? '').trim(),
        difficultyHint: String(formData.get('difficultyHint') ?? '').trim(),
        title,
        topics,
    });

    if (isErr(result)) {
        return { error: 'Could not create module.' };
    }

    return redirect('/admin/modules');
});

export default function AdminNewModuleRoute() {
    const actionData = useActionData() as { error?: string } | undefined;
    const navigation = useNavigation();
    const isSubmitting = navigation.state === 'submitting';
    const codeId = useId();
    const titleId = useId();
    const descriptionId = useId();
    const difficultyId = useId();

    return (
        <div className={adminModuleFormPageClassName}>
            <Card>
                <CardTitle>Create module</CardTitle>
                <CardDescription>Add a new course module with topics and difficulty hints.</CardDescription>
            </Card>

            <Card>
                <Form className="flex flex-col gap-6" method="post">
                    <div className="grid gap-4 sm:grid-cols-2">
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={codeId}>Code</Label>
                            <Input id={codeId} name="code" placeholder="e.g. DWT" required />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={titleId}>Title</Label>
                            <Input id={titleId} name="title" required />
                        </div>
                        <div className="flex flex-col gap-2 sm:col-span-2">
                            <Label htmlFor={descriptionId}>Description</Label>
                            <Textarea id={descriptionId} name="description" />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={difficultyId}>Difficulty</Label>
                            <Input id={difficultyId} name="difficultyHint" placeholder="Easy / Medium / Hard" />
                        </div>
                    </div>

                    <ModuleTopicEditor />

                    {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}

                    <div className="flex flex-wrap gap-2">
                        <Button disabled={isSubmitting} type="submit">
                            {isSubmitting ? 'Creating…' : 'Create module'}
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
