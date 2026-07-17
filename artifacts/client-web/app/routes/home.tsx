import type { LoaderFunctionArgs } from 'react-router';
import { Link, redirect } from 'react-router';
import { getSessionUser } from '~/.server/service/session';
import { contentMaxWidth } from '~/components/shell';
import { buttonVariants } from '~/components/ui/button';
import { cn } from '~/lib/ui/utils';

const valuePillars = [
    {
        description:
            'Filter tutors by module, language, place, and hourly rate — focused on the courses you are taking, not generic tutoring platforms.',
        title: 'Discovery that fits your courses',
    },
    {
        description:
            'Compare tutors by topic coverage, availability, and hourly rate so you know who fits your module and goals.',
        title: 'Trust before you connect',
    },
    {
        description:
            'Each module highlights key topics and difficulty hints, guiding students and tutors toward what matters most.',
        title: 'Know where to focus',
    },
] as const;

const howItWorksSteps = [
    {
        description: 'Pick a module, topics, target date, and budget.',
        step: '1',
        title: 'Set your goal',
    },
    {
        description: 'Search and filter tutors who match your course and preferences.',
        step: '2',
        title: 'Discover tutors',
    },
    {
        description: 'GenAI suggests a catch-up or semester plan using live tutor and module data.',
        step: '3',
        title: 'Get a plan',
    },
    {
        description: 'Message your tutor and arrange sessions off-platform.',
        step: '4',
        title: 'Start learning',
    },
] as const;

const studentScenarios = [
    {
        description: 'Get a catch-up plan ordered by topic impact when time is short.',
        title: 'Late start before an exam',
    },
    {
        description: 'Find affordable tutors and plans that respect your weekly budget.',
        title: 'Budget-conscious learning',
    },
    {
        description: 'Filter tutors by language when you need support in English or German.',
        title: 'Language-friendly help',
    },
] as const;

const REGISTER_REDIRECT = encodeURIComponent('/me/profile?edit=1');

export async function loader({ request }: LoaderFunctionArgs) {
    const userResult = await getSessionUser(request);
    if (userResult.isOk && userResult.value) {
        throw redirect('/dashboard');
    }

    return { user: null };
}

export default function HomeRoute() {
    return (
        <div className="flex flex-col">
            <section className="border-b border-border bg-muted/30">
                <div className={cn('mx-auto flex w-full flex-col gap-8 py-16 md:py-24', contentMaxWidth.wide)}>
                    <div className="flex max-w-3xl flex-col gap-4">
                        <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                            Personal tutoring for your courses
                        </p>
                        <h1 className="text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
                            Find personal help for TUM courses — matched to your budget and deadline.
                        </h1>
                        <p className="text-base leading-relaxed text-muted-foreground md:text-lg">
                            Stop searching WhatsApp groups. TUtorMatch connects you with tutors who understand your
                            modules and exam timelines, then helps you build a personalized study plan from real
                            availability and course context.
                        </p>
                    </div>

                    <div className="flex flex-wrap gap-3">
                        <Link
                            className={cn(buttonVariants({ size: 'lg' }))}
                            to={`/auth/register?redirectTo=${REGISTER_REDIRECT}`}
                        >
                            Create free account
                        </Link>
                        <Link className={cn(buttonVariants({ size: 'lg', variant: 'outline' }))} to="/login">
                            Sign in
                        </Link>
                        <Link className={cn(buttonVariants({ size: 'lg', variant: 'outline' }))} to="/become-a-tutor">
                            Become a tutor
                        </Link>
                    </div>
                </div>
            </section>

            <section className="border-b border-border">
                <div className={cn('mx-auto grid w-full gap-8 py-16 md:grid-cols-3', contentMaxWidth.wide)}>
                    {valuePillars.map((pillar) => (
                        <div className="flex flex-col gap-2" key={pillar.title}>
                            <h2 className="text-base font-semibold text-foreground">{pillar.title}</h2>
                            <p className="text-sm leading-relaxed text-muted-foreground">{pillar.description}</p>
                        </div>
                    ))}
                </div>
            </section>

            {/* biome-ignore lint/correctness/useUniqueElementIds: stable in-page anchor linked from public nav */}
            <section className="border-b border-border" id="how-it-works">
                <div className={cn('mx-auto w-full py-16', contentMaxWidth.wide)}>
                    <div className="mb-10 flex max-w-2xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">How it works</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            From learning goal to tutor conversation — built for students who want course-specific
                            support.
                        </p>
                    </div>

                    <ol className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                        {howItWorksSteps.map((item) => (
                            <li
                                className="flex flex-col gap-3 rounded-lg border border-border bg-card p-5"
                                key={item.step}
                            >
                                <span className="flex size-8 items-center justify-center rounded-full bg-primary text-xs font-semibold text-primary-foreground">
                                    {item.step}
                                </span>
                                <div className="flex flex-col gap-1">
                                    <h3 className="text-sm font-semibold text-foreground">{item.title}</h3>
                                    <p className="text-sm leading-relaxed text-muted-foreground">{item.description}</p>
                                </div>
                            </li>
                        ))}
                    </ol>
                </div>
            </section>

            <section className="border-b border-border bg-muted/20">
                <div className={cn('mx-auto w-full py-16', contentMaxWidth.wide)}>
                    <div className="mb-8 flex max-w-2xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">
                            Course modules we know well
                        </h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Explore topics from popular university courses and see where your fellow students struggle
                            most before you choose a tutor to tackle these obstacles.
                        </p>
                    </div>
                    <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/modules">
                        Browse modules
                    </Link>
                </div>
            </section>

            <section className="border-b border-border">
                <div className={cn('mx-auto w-full py-16', contentMaxWidth.wide)}>
                    <div className="mb-8 flex max-w-2xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">For students</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Whether you started late, need a target grade, or want help in a specific course.
                        </p>
                    </div>

                    <div className="grid gap-4 md:grid-cols-3">
                        {studentScenarios.map((scenario) => (
                            <div className="rounded-lg border border-border p-5" key={scenario.title}>
                                <h3 className="text-sm font-semibold text-foreground">{scenario.title}</h3>
                                <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                                    {scenario.description}
                                </p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            <section className="border-b border-border bg-muted/20">
                <div
                    className={cn(
                        'mx-auto flex w-full flex-col gap-4 py-16 md:flex-row md:items-center md:justify-between',
                        contentMaxWidth.wide,
                    )}
                >
                    <div className="flex max-w-xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">For tutors</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Earn side income by teaching modules you excel at. Set your modules, rate, languages, and
                            availability — students find you through targeted discovery.
                        </p>
                    </div>
                    <Link className={cn(buttonVariants())} to="/become-a-tutor">
                        Apply as tutor
                    </Link>
                </div>
            </section>

            <section>
                <div
                    className={cn(
                        'mx-auto flex w-full flex-col items-start gap-6 py-16 md:flex-row md:items-center md:justify-between',
                        contentMaxWidth.wide,
                    )}
                >
                    <div className="flex max-w-xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">
                            Ready to stop searching WhatsApp groups?
                        </h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Create an account, set up your profile, and discover tutors matched to the modules you are
                            studying.
                        </p>
                    </div>
                    <div className="flex flex-wrap gap-3">
                        <Link className={cn(buttonVariants())} to={`/auth/register?redirectTo=${REGISTER_REDIRECT}`}>
                            Create free account
                        </Link>
                        <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/modules">
                            Browse modules
                        </Link>
                    </div>
                </div>
            </section>
        </div>
    );
}
