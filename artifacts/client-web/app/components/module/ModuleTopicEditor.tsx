import { useState } from 'react';
import { STUDY_FOCUS_FIELDS, StudyFocusRating } from '~/components/profile';
import { Button } from '~/components/ui/button';
import { Card, CardDescription, CardTitle } from '~/components/ui/card';
import { Input } from '~/components/ui/input';
import { Label } from '~/components/ui/label';
import { Textarea } from '~/components/ui/textarea';
import { emptyTopicDraft, type TopicDraft } from './moduleFormData';

type TopicRow = TopicDraft & { key: string };

function toTopicRows(topics: TopicDraft[]): TopicRow[] {
    return topics.map((topic) => ({ ...topic, key: crypto.randomUUID() }));
}

type ModuleTopicEditorProps = {
    initialTopics?: TopicDraft[];
};

export function ModuleTopicEditor({ initialTopics = [] }: ModuleTopicEditorProps) {
    const [topics, setTopics] = useState<TopicRow[]>(
        initialTopics.length > 0 ? toTopicRows(initialTopics) : toTopicRows([emptyTopicDraft()]),
    );

    return (
        <div className="flex flex-col gap-4">
            <input name="topicCount" type="hidden" value={topics.length} />
            <div className="flex items-center justify-between gap-4">
                <div>
                    <h2 className="text-base font-semibold">Topics</h2>
                    <p className="text-sm text-muted-foreground">Add, edit, or remove topics for this module.</p>
                </div>
                <Button
                    onClick={() =>
                        setTopics((current) => [...current, { ...emptyTopicDraft(), key: crypto.randomUUID() }])
                    }
                    type="button"
                    variant="outline"
                >
                    Add topic
                </Button>
            </div>
            {topics.map((topic, index) => (
                <Card key={topic.key}>
                    <div className="flex items-start justify-between gap-4">
                        <CardTitle className="text-base">Topic {index + 1}</CardTitle>
                        {topics.length > 1 ? (
                            <Button
                                onClick={() => setTopics((current) => current.filter((_, i) => i !== index))}
                                size="sm"
                                type="button"
                                variant="outline"
                            >
                                Remove
                            </Button>
                        ) : null}
                    </div>
                    <div className="mt-4 grid gap-4">
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={`topic_${index}_name`}>Name</Label>
                            <Input
                                defaultValue={topic.name}
                                id={`topic_${index}_name`}
                                name={`topic_${index}_name`}
                                required
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <Label htmlFor={`topic_${index}_description`}>Description</Label>
                            <Textarea
                                defaultValue={topic.description}
                                id={`topic_${index}_description`}
                                name={`topic_${index}_description`}
                            />
                        </div>
                        <div className="flex flex-col gap-2 sm:max-w-xs">
                            <Label htmlFor={`topic_${index}_difficultyHint`}>Difficulty</Label>
                            <Input
                                defaultValue={topic.difficultyHint}
                                id={`topic_${index}_difficultyHint`}
                                name={`topic_${index}_difficultyHint`}
                                placeholder="Easy / Medium / Hard"
                            />
                        </div>
                        <fieldset className="flex flex-col gap-4">
                            <legend className="text-sm font-medium text-foreground">Study focus</legend>
                            {STUDY_FOCUS_FIELDS.map((field) => (
                                <div
                                    className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between"
                                    key={field.key}
                                >
                                    <div className="flex min-w-0 flex-col gap-0.5">
                                        <Label className="font-normal">{field.label}</Label>
                                        <CardDescription>{field.description}</CardDescription>
                                    </div>
                                    <StudyFocusRating
                                        defaultValue={topic.studyFocus[field.key]}
                                        name={`topic_${index}_studyFocus_${field.key}`}
                                    />
                                </div>
                            ))}
                        </fieldset>
                    </div>
                </Card>
            ))}
        </div>
    );
}
