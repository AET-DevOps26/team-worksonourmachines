import { Form, Link, useRevalidator, useRouteError } from 'react-router';
import { PageContainer } from '~/components/shell';
import { Button, buttonVariants } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { cn } from '~/lib/ui/utils';
import { getRouteErrorPresentation } from './routeErrorPresentation';

type RouteErrorPageProps = {
    standalone?: boolean;
};

export function RouteErrorPage({ standalone = false }: RouteErrorPageProps) {
    const error = useRouteError();
    const presentation = getRouteErrorPresentation(error);
    const revalidator = useRevalidator();

    return (
        <div className={standalone ? 'flex min-h-svh items-center bg-background py-8 text-foreground' : undefined}>
            <PageContainer>
                <Card className="mx-auto max-w-xl">
                    <p className="text-sm font-medium text-destructive">Error</p>
                    <CardTitle className="mt-2 text-2xl">{presentation.title}</CardTitle>
                    <CardDescription className="mt-2">{presentation.description}</CardDescription>
                    <div className="mt-6 flex flex-wrap gap-3">
                        {presentation.signInAgain ? (
                            <Form action="/auth/logout" method="post">
                                <Button type="submit">Sign in again</Button>
                            </Form>
                        ) : null}
                        {presentation.retry ? (
                            <Button
                                disabled={revalidator.state !== 'idle'}
                                onClick={() => revalidator.revalidate()}
                                type="button"
                            >
                                {revalidator.state === 'idle' ? 'Try again' : 'Trying again…'}
                            </Button>
                        ) : null}
                        {!presentation.signInAgain ? (
                            <Link
                                className={cn(buttonVariants({ variant: presentation.retry ? 'outline' : 'default' }))}
                                to={presentation.linkTo}
                            >
                                {presentation.linkLabel}
                            </Link>
                        ) : null}
                    </div>
                </Card>
            </PageContainer>
        </div>
    );
}
