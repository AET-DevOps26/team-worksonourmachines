import { Link } from 'react-router';
import { contentMaxWidth } from '~/components/shell';
import { buttonVariants } from '~/components/ui/button';
import { cn } from '~/lib/ui/utils';

const struggles = [
    {
        description:
            'You know the feeling: another attempt, the same module, and the gap between what the lecture expects and what actually clicks for you keeps growing.',
        title: 'Stuck on yet another retake',
    },
    {
        description:
            'Generic tutoring platforms and one-size-fits-all explanations rarely match how your course is taught, which topics matter for the exam, or where students typically get lost.',
        title: 'Help that does not fit your course',
    },
    {
        description:
            'Many students pay more and still get less — high fees for tutors who never took your module, scattered WhatsApp recommendations, and no way to tell who actually understands the material.',
        title: 'Paying more, progressing less',
    },
] as const;

const missionPoints = [
    'Make it easy to find tutors who know the specific modules you are struggling with.',
    'Surface course context — topics, difficulty, where to focus — so sessions start useful, not from zero.',
    'Give students on a budget filters for language, format, and price without settling for generic help.',
    'Let strong students earn fairly by tutoring the courses they have already mastered.',
] as const;

export default function AboutRoute() {
    return (
        <div className="-mx-6 flex flex-col">
            <section className="border-b border-border bg-muted/30">
                <div className={cn('mx-auto flex w-full flex-col gap-6 px-6 py-16 md:py-24', contentMaxWidth.wide)}>
                    <div className="flex max-w-3xl flex-col gap-4">
                        <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">About us</p>
                        <h1 className="text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
                            Built by students who have been there.
                        </h1>
                        <p className="text-base leading-relaxed text-muted-foreground md:text-lg">
                            TUtorMatch started with a simple observation: we are students ourselves, and we know how
                            hard it is to get proper help when a course stops making sense. Not vague advice — help that
                            matches the module you are in, the exam you are facing, and the budget you actually have.
                        </p>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            We are not part of any university. We built this platform because the existing options did
                            not solve the problem we kept running into.
                        </p>
                    </div>
                </div>
            </section>

            <section className="border-b border-border">
                <div className={cn('mx-auto w-full px-6 py-16', contentMaxWidth.wide)}>
                    <div className="mb-10 flex max-w-2xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">Why we built this</h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            Too many of us have spent evenings searching group chats, comparing random recommendations,
                            or paying for tutoring that never quite matched the course.
                        </p>
                    </div>

                    <div className="grid gap-6 md:grid-cols-3">
                        {struggles.map((item) => (
                            <div className="flex flex-col gap-2 rounded-lg border border-border p-5" key={item.title}>
                                <h3 className="text-sm font-semibold text-foreground">{item.title}</h3>
                                <p className="text-sm leading-relaxed text-muted-foreground">{item.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            <section className="border-b border-border bg-muted/20">
                <div className={cn('mx-auto w-full px-6 py-16', contentMaxWidth.wide)}>
                    <div className="grid gap-10 md:grid-cols-2 md:items-start">
                        <div className="flex flex-col gap-3">
                            <h2 className="text-2xl font-semibold tracking-tight text-foreground">Our mission</h2>
                            <p className="text-sm leading-relaxed text-muted-foreground">
                                Help everyone who is stuck find support that actually fits — the right module, the right
                                person, and a path forward before the next exam date slips away.
                            </p>
                            <p className="text-sm leading-relaxed text-muted-foreground">
                                Whether you are behind on material, aiming for a target grade, learning in a second
                                language, or trying not to overspend, you deserve tutoring that understands your course
                                — not a generic substitute.
                            </p>
                        </div>

                        <ul className="flex flex-col gap-3">
                            {missionPoints.map((point) => (
                                <li className="flex gap-3 text-sm leading-relaxed text-muted-foreground" key={point}>
                                    <span aria-hidden className="mt-1.5 size-1.5 shrink-0 rounded-full bg-primary" />
                                    {point}
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </section>

            <section>
                <div
                    className={cn(
                        'mx-auto flex w-full flex-col items-start gap-6 px-6 py-16 md:flex-row md:items-center md:justify-between',
                        contentMaxWidth.wide,
                    )}
                >
                    <div className="flex max-w-xl flex-col gap-2">
                        <h2 className="text-2xl font-semibold tracking-tight text-foreground">
                            That is what we are working toward.
                        </h2>
                        <p className="text-sm leading-relaxed text-muted-foreground">
                            If this sounds familiar, you are exactly who we built TUtorMatch for.
                        </p>
                    </div>
                    <div className="flex flex-wrap gap-3">
                        <Link className={cn(buttonVariants())} to="/login?redirectTo=%2Fdiscover">
                            Find a tutor
                        </Link>
                        <Link className={cn(buttonVariants({ variant: 'outline' }))} to="/become-a-tutor">
                            Become a tutor
                        </Link>
                    </div>
                </div>
            </section>
        </div>
    );
}
