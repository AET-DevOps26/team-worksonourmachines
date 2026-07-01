import { useId } from 'react';
import { Form, Link, useActionData, useNavigation } from 'react-router';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Textarea } from '~/components/ui/textarea';
import { StudyFocusRating } from './StudyFocusRating';

const STUDY_FOCUS_FIELDS = [
    {
        description: 'Recalling definitions, formulas, and terminology.',
        key: 'memorization' as const,
        label: 'Memorization',
    },
    {
        description: 'Proofs, logic, derivations, and symbolic manipulation.',
        key: 'formalReasoning' as const,
        label: 'Formal reasoning',
    },
    {
        description: 'Building mental models and understanding why concepts work.',
        key: 'conceptualUnderstanding' as const,
        label: 'Conceptual understanding',
    },
    {
        description: 'Applying theory to exercises and exam-style problems.',
        key: 'problemSolving' as const,
        label: 'Problem solving',
    },
] as const;

type StudyFocus = {
    [K in (typeof STUDY_FOCUS_FIELDS)[number]['key']]: number;
};

type StudentProfileFormProps = {
    action?: string;
    bio: string;
    displayName: string;
    languages: string;
    studyFocus?: StudyFocus;
    submitLabel: string;
    title: string;
    description: string;
    showSkipLink?: boolean;
};

export function StudentProfileForm({
    action,
    bio,
    description,
    displayName,
    languages,
    studyFocus,
    showSkipLink = true,
    submitLabel,
    title,
}: StudentProfileFormProps) {
    const navigation = useNavigation();
    const actionData = useActionData() as { error?: string } | undefined;
    const isSubmitting = navigation.state === 'submitting';
    const displayNameId = useId();
    const bioId = useId();
    const languagesId = useId();

    return (
        <div className="mx-auto flex w-full max-w-2xl flex-col gap-6">
            <Card>
                <CardTitle>{title}</CardTitle>
                <CardDescription>{description}</CardDescription>
            </Card>
            <Card>
                <Form className="flex flex-col gap-4" method="post" {...(action ? { action } : {})}>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={displayNameId}>Display name</Label>
                        <Input defaultValue={displayName} id={displayNameId} name="displayName" required />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={bioId}>Bio</Label>
                        <Textarea defaultValue={bio} id={bioId} name="bio" />
                    </div>
                    <div className="flex flex-col gap-2">
                        <Label htmlFor={languagesId}>Languages (comma-separated)</Label>
                        <Input
                            defaultValue={languages}
                            id={languagesId}
                            name="languages"
                            placeholder="German, English"
                        />
                    </div>
                    <fieldset className="flex flex-col gap-4">
                        <legend className="text-sm font-medium text-foreground">
                            Study focus (optional — tap stars, 1 = needs work, 5 = confident)
                        </legend>
                        {STUDY_FOCUS_FIELDS.map((field) => (
                            <div
                                className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between"
                                key={field.key}
                            >
                                <div className="flex min-w-0 flex-col gap-0.5">
                                    <Label className="font-normal">{field.label}</Label>
                                    <span className="text-xs text-muted-foreground">{field.description}</span>
                                </div>
                                <StudyFocusRating
                                    defaultValue={studyFocus?.[field.key] ?? 3}
                                    name={`studyFocus_${field.key}`}
                                />
                            </div>
                        ))}
                    </fieldset>
                    {actionData?.error ? <p className="text-sm text-destructive">{actionData.error}</p> : null}
                    <Button disabled={isSubmitting} type="submit">
                        {isSubmitting ? 'Saving…' : submitLabel}
                    </Button>
                </Form>
            </Card>
            {showSkipLink ? (
                <Link className="text-sm text-muted-foreground hover:text-foreground" to="/discover">
                    Skip to discover tutors
                </Link>
            ) : null}
        </div>
    );
}

export function parseLanguages(value: string): string[] {
    return value
        .split(',')
        .map((l) => l.trim())
        .filter(Boolean);
}

export function formatLanguages(languages: string[]): string {
    return languages.join(', ');
}

export function parseStudyFocusFromFormData(formData: FormData): StudyFocus | undefined {
    const focus: Partial<StudyFocus> = {};
    let hasAny = false;

    for (const field of STUDY_FOCUS_FIELDS) {
        const raw = formData.get(`studyFocus_${field.key}`);
        if (raw === null || raw === '') {
            continue;
        }
        const value = Number(raw);
        if (!Number.isInteger(value) || value < 1 || value > 5) {
            return undefined;
        }
        focus[field.key] = value;
        hasAny = true;
    }

    if (!hasAny) {
        return undefined;
    }

    for (const field of STUDY_FOCUS_FIELDS) {
        if (focus[field.key] === undefined) {
            return undefined;
        }
    }

    return focus as StudyFocus;
}

export function formatStudyFocusLabel(key: keyof StudyFocus): string {
    return STUDY_FOCUS_FIELDS.find((field) => field.key === key)?.label ?? key;
}

export { STUDY_FOCUS_FIELDS };
