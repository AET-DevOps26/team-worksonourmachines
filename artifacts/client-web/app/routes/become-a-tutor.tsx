import { Link } from 'react-router';
import { buttonVariants } from '~/components/ui/button';
import { cn } from '~/lib/ui/utils';

const benefits = [
    {
        description:
            'Students find you through module-specific search — not random DMs in group chats. You choose which courses you teach.',
        title: 'Reach students who need your modules',
    },
    {
        description:
            'Set your hourly rate, languages, and whether you prefer campus, online, or both. You stay in control of your schedule.',
        title: 'Work on your terms',
    },
    {
        description:
            'Topic difficulty hints and module context help students know what they struggle with — so sessions start focused.',
        title: 'Teach with course context',
    },
] as const;

const steps = [
    {
        description: 'Pick the modules you want to tutor and upload proof that you have passed them.',
        step: '1',
        title: 'Apply',
    },
    {
        description: 'Our team reviews your application. Once approved, you can publish your tutor profile.',
        step: '2',
        title: 'Get verified',
    },
    {
        description: 'Add your bio, coverage, availability, and rate — then make your profile visible to students.',
        step: '3',
        title: 'Publish your profile',
    },
    {
        description: 'Students discover you, send messages, and arrange sessions with you directly.',
        step: '4',
        title: 'Start tutoring',
    },
] as const;

const requirements = [
    'You have passed the modules you want to tutor (certificate upload required).',
    'You can describe your experience clearly in a short bio.',
    'You set your own rate and availability — TUtorMatch does not process payments.',
    'You respond to student messages through the platform chat.',
] as const;

export default function BecomeATutorRoute() {
    return (
        <div className="-mx-6 flex flex-col">
            <section className="border-b border-border bg-muted/30">
                <div className="mx-auto flex w-full max-w-6xl flex-col gap-8 px-6 py-16 md:py-24">
                    <div className="flex max-w-3xl flex-col gap-4">
                        <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">For tutors</p>
                        <h1 className="text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
                            Turn your course knowledge into tutoring — on your schedule.
                        </h1>
                        <p className="text-base leading-relaxed text-muted-foreground md:text-lg">
                            TUtorMatch helps students find tutors who know their modules. If you excel in courses others
                            struggle with, list your expertise, set your rate, and connect with students who are looking
                            for exactly that kind of help.
                        </p>
                    </div>

                    <div className="flex flex-wrap gap-3">
                        <Link className={cn(buttonVariants({ size: 'lg' }))} to="/tutor/apply">
                            Apply as tutor
                        </Link>
                        <Link
                            className={cn(buttonVariants({ size: 'lg', variant: 'outline' }))}
                            to="/login?redirectTo=%2Ftutor%2Fapply"
                        >
                            Log in to apply
                        </Link>
                    </div>
                </div>
            </section>

            <section className="border-b border-border">
                <div className="mx-auto grid w-full max-w-6xl gap-8 px-6 py-16 md:grid-cols-3">
                    {benefits.map((benefit) => (
                        <div className="flex flex-col gap-2" key={benefit.title}>
                            <h2 className="text-base font-semibold text-foreground">{benefit.title}</h2>
                            <p className="text-sm leading-relaxed text-muted-foreground">{benefit.description}</p>
                        </div>
                    ))}
                </div>
            </section>

            <section className="border-b border-border bg-muted/20">
                <div className="mx-auto w-full max-w-6xl px-6 py-16">
                    <div className="mb-10 flex max-w-2xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">How it works</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            From application to your first student conversation — a straightforward path.
                        </p>
                    </div>

                    <ol className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                        {steps.map((item) => (
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

            <section className="border-b border-border">
                <div className="mx-auto w-full max-w-6xl px-6 py-16">
                    <div className="grid gap-10 md:grid-cols-2">
                        <div className="flex flex-col gap-2">
                            <h2 className="text-2xl font-semibold tracking-tight text-foreground">What we expect</h2>
                            <p className="text-sm leading-relaxed text-muted-foreground">
                                TUtorMatch is independent of any university. We verify tutors so students can trust who
                                they contact.
                            </p>
                        </div>

                        <ul className="flex flex-col gap-3">
                            {requirements.map((item) => (
                                <li className="flex gap-3 text-sm leading-relaxed text-muted-foreground" key={item}>
                                    <span aria-hidden className="mt-1.5 size-1.5 shrink-0 rounded-full bg-primary" />
                                    {item}
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </section>

            <section>
                <div className="mx-auto flex w-full max-w-6xl flex-col items-start gap-6 px-6 py-16 md:flex-row md:items-center md:justify-between">
                    <div className="flex max-w-xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">Ready to get started?</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Submit your application in a few minutes. Once approved, you can publish your profile and
                            appear in student search results.
                        </p>
                    </div>
                    <Link className={cn(buttonVariants({ size: 'lg' }))} to="/tutor/apply">
                        Start your application
                    </Link>
                </div>
            </section>
        </div>
    );
}
