# AI Component — Inputs & Outputs

The AI component is a FastAPI service that wraps a configurable LLM (Ollama, LM Studio, OpenAI, or Logos) and exposes two endpoints: a generic `/v1/chat` endpoint and a `/v1/plan` endpoint for generating personalised study plans.

---

## Plan Generation (`POST /v1/plan`)

### Inputs

| Field | Type | Description |
|---|---|---|
| `student.id` | string | Unique student identifier |
| `student.displayName` | string | Display name |
| `student.bio` | string | Free-text self-description — gives the AI context about the student's background |
| `student.languages` | string[] | e.g. `["en", "de"]` — **primary filter for tutor selection**; only tutors who teach in at least one of these languages are considered. Falls back to all tutors if none match. |
| `student.studyFocus` | `StudyFocus` | Self-assessed proficiency per study skill (1 = needs work, 5 = confident) — used to prioritise tutor matching and session sequencing |
| `context` | string | Optional free-text from the student — e.g. "I struggle with proofs but exam is in 3 weeks" — passed directly into the LLM prompt for richer personalisation |
| `studyGoal` | `"pass" \| "good_grade" \| "top_grade"` | The student's target outcome — influences how intensively the AI schedules sessions and selects tutors |
| `course.id` | string | Course / module identifier |
| `course.name` | string | Human-readable course name (e.g. "Linear Algebra") |
| `course.topics` | `Topic[]` | Ordered list of topics with difficulty levels and study-focus weights |
| `milestone.id` | string | Goal identifier (e.g. exam session) |
| `milestone.name` | string | Human-readable goal name |
| `milestone.dueDate` | ISO 8601 datetime | Deadline — the plan fits sessions before this date |
| `milestone.completed` | boolean | Whether the milestone is already done |

Tutor data (rates, languages, topic coverage, weekday availability) is fetched internally by the AI service from the Marketplace API and injected into the LLM prompt — it is not passed by the caller.

#### `StudyFocus` shape
```json
{
  "memorization": 3,
  "formalReasoning": 2,
  "conceptualUnderstanding": 4,
  "problemSolving": 3
}
```
Each dimension is rated 1–5. For the student this reflects **self-assessed proficiency** (where they need most help). For topics it reflects the **inherent study demands** of that subject. The AI uses both to match tutors and sequence sessions — e.g. a student weak in `formalReasoning` on a topic with high `formalReasoning` demand gets prioritised.

#### `Topic` shape
```json
{
  "id": "t1",
  "name": "Matrix Decomposition",
  "difficultyHint": "Hard",
  "studyFocus": { "memorization": 2, "formalReasoning": 5, "conceptualUnderstanding": 4, "problemSolving": 4 }
}
```

#### Budget
The budget is not a top-level field in the current API contract — it is **derived** from the student context and the number of sessions the AI schedules.

> **Planned extension:** add an explicit `budgetEur` field to `GeneratePlanRequest` so the AI can generate the three suggestion tiers described below without requiring three separate calls.

---

### Output — three suggestion tiers

The AI generates (or should generate) **three ranked suggestions** for each request, covering the following scenarios:

| Tier | Label | Description |
|---|---|---|
| 1 | **Cheapest option** | Schedule built exclusively from the lowest-rate tutors who cover the required topics. Total cost is minimised; may not cover all topics if no cheap tutor covers them. |
| 2 | **Within budget** | Balanced plan that stays inside the student's stated budget while covering all topics. Prefers higher-rated tutors when cost headroom allows. |
| 3 | **Best quality** | Highest-rated or most experienced tutors for every topic, regardless of cost. Total may exceed the base budget. |

Each suggestion returns a `StudyPlan`:

| Field | Type | Description |
|---|---|---|
| `planId` | string | Unique identifier for this plan |
| `milestoneId` | string | The goal this plan targets |
| `status` | `"pending" \| "ready" \| "failed"` | Async generation status |
| `mode` | `"standard" \| "cost_optimized"` | Mode used |
| `schedule` | `ScheduleEntry[]` | Ordered list of suggested sessions |
| `totalEstimatedCost` | float | Sum of all session costs in EUR |

#### `ScheduleEntry` shape
```json
{
  "weekday": "wednesday",
  "topicId": "t1",
  "topicName": "Matrix Decomposition",
  "tutorId": "u1",
  "tutorName": "Anna Schmidt",
  "estimatedCost": 18.00
}
```
`weekday` reflects a recurring day of the week on which the session is scheduled, matching the tutor's availability.

---

## Chat endpoint (`POST /v1/chat`)

A simple pass-through to the configured LLM, used for ad-hoc questions.

| Field | Type | Description |
|---|---|---|
| `prompt` | string | Free-text prompt |

Returns `{ "message": string }`.

---

## LLM Provider configuration

Set via environment variables in `.env` (see `.env.dist`):

| Variable | Default | Description |
|---|---|---|
| `LLM_PROVIDER` | `ollama` | `ollama`, `lmstudio`, `openai`, or `logos` |
| `LLM_MODEL` | model-dependent | Model name/identifier |
| `LLM_BASE_URL` | provider-dependent | Base URL override |
| `LLM_API_KEY` | — | Required for `openai` and `logos` |

---

## Async flow

`POST /v1/plan` returns `202 Accepted` with `{ "planId": "..." }`.  
Poll `GET /v1/plan/{planId}` until `status` is `"ready"` or `"failed"`.

---

## LLM prompt blueprint

The AI service assembles all inputs into a single structured prompt before calling the LLM. Below is the blueprint — `{{placeholders}}` are filled at runtime.

```
You are an academic study planner. Your task is to suggest a personalised tutoring schedule for a university student.

## Student
- Name: {{student.displayName}}
- About: {{student.bio}}
- Teaching language preference: {{student.languages | join(", ")}}
- Self-assessed study skills (1 = needs work, 5 = confident):
  - Memorization: {{student.studyFocus.memorization}}
  - Formal reasoning: {{student.studyFocus.formalReasoning}}
  - Conceptual understanding: {{student.studyFocus.conceptualUnderstanding}}
  - Problem solving: {{student.studyFocus.problemSolving}}

## Goal
- Course: {{course.name}}
- Study goal: {{studyGoal}}  {# pass | good_grade | top_grade #}
- Milestone: {{milestone.name}} on {{milestone.dueDate}}
- Additional context from the student: {{context | default("none")}}

## Topics to cover
{{for topic in course.topics}}
- {{topic.name}} (difficulty: {{topic.difficultyHint}})
  Study demands — memorization: {{topic.studyFocus.memorization}}, formal reasoning: {{topic.studyFocus.formalReasoning}}, conceptual understanding: {{topic.studyFocus.conceptualUnderstanding}}, problem solving: {{topic.studyFocus.problemSolving}}
{{endfor}}

## Available tutors
{{for tutor in tutors}}
- {{tutor.displayName}} | €{{tutor.hourlyRate}}/h | rating: {{tutor.ratingSummary.average}} ({{tutor.ratingSummary.count}} sessions)
  Languages: {{tutor.languages | join(", ")}}
  Locations: {{tutor.locations | join(", ")}}
  Available on: {{tutor.availability | where("available", true) | map("weekday") | join(", ")}}
  Covers topics: {{tutor.topicIds | join(", ")}}
{{endfor}}

## Instructions
Generate THREE ranked study plan suggestions:
1. **Cheapest option** — minimise total cost; use the lowest-rate tutors who cover each topic.
2. **Within budget** — stay within the student's budget while preferring higher-rated tutors where possible.
3. **Best quality** — use the highest-rated tutors for every topic regardless of cost.

Rules:
- Only assign tutors who cover the topic AND teach in one of the student's preferred languages (fall back to all tutors if none match).
- Schedule sessions on weekdays when the tutor is available.
- Prioritise topics where the student's weak skills (low studyFocus scores) overlap with the topic's high demands.
- A student aiming to "pass" needs fewer sessions than one aiming for "top_grade" — scale intensity accordingly.
- Do not suggest booking or transactions — this platform is for finding tutors only.
- All sessions must fall before {{milestone.dueDate}}.

## Output format
Return a JSON array of exactly three plan objects in this shape:
[
  {
    "tier": "cheapest" | "within_budget" | "best_quality",
    "totalEstimatedCost": <float>,
    "schedule": [
      {
        "weekday": "<weekday>",
        "topicId": "<id>",
        "topicName": "<name>",
        "tutorId": "<id>",
        "tutorName": "<name>",
        "estimatedCost": <float>
      }
    ]
  }
]
Return only the JSON array — no prose, no markdown fences.
```
