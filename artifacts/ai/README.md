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
| `student.location` | string | Student's city / location — used to filter tutors who offer in-person sessions nearby |
| `student.studyFocus` | `StudyFocus` | Self-assessed proficiency per study skill (1 = needs work, 5 = confident) — used to prioritise tutor matching and session sequencing |
| `dueDate` | ISO 8601 datetime | Target date by which the student wants to complete the plan (e.g. exam date) |
| `description` | string | Free-text description of the learning goal — what the student wants to achieve |
| `budgetEur` | float | Maximum budget in EUR the student is willing to spend across all sessions |
| `course.id` | string | Course / module identifier |
| `course.name` | string | Human-readable course name (e.g. "Linear Algebra") |
| `course.topics` | `Topic[]` | Ordered list of topics with difficulty levels and study-focus weights |


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
The student's budget is passed explicitly as `budgetEur` (EUR). The AI uses it to define the "within budget" tier and as a reference for the other two tiers.

---

### Output — three suggestion tiers

The AI generates (or should generate) **three ranked suggestions** for each request, covering the following scenarios:

| Tier | Label | Description |
|---|---|---|
| 1 | **Cheapest option** | Schedule built exclusively from the lowest-rate tutors who cover the required topics. Total cost is minimised; may not cover all topics or may have more tutor changes if no cheap tutor covers them. |
| 2 | **Within budget** | Balanced plan that stays inside the student's stated budget while covering all topics if feasible. Prefers higher-rated tutors when cost headroom allows. |
| 3 | **Best quality** | Highest-rated or most experienced tutors for every topic, regardless of cost. Total may exceed the base budget. 


Each suggestion returns a `StudyPlan` with an array of milestones (one per topic) and a schedule:

| Field | Type | Description |
|---|---|---|
| `planId` | string | Unique identifier for this plan |
| `milestones` | `Milestone[]` | One milestone per topic — ordered checkpoints leading to the due date |
| `milestone.id` | string | Unique milestone identifier |
| `milestone.title` | string | Human-readable milestone name, e.g. "Master Matrix Decomposition" |
| `milestone.dueDate` | ISO 8601 datetime | Target completion date for this topic milestone |
| `suggestedTutors` | `Tutor[]` | Deduplicated list of tutors assigned in this plan — ideally one, as few as possible |
| `mode` | `"standard" \| "cost_optimized"` | Mode used |
| `schedule` | `ScheduleEntry[]` | Ordered list of suggested sessions - which tutor for which topic |
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
- Location: {{student.location}}
- Teaching language preference: {{student.languages | join(", ")}}
- Self-assessed study skills (1 = needs work, 5 = confident):
  - Memorization: {{student.studyFocus.memorization}}
  - Formal reasoning: {{student.studyFocus.formalReasoning}}
  - Conceptual understanding: {{student.studyFocus.conceptualUnderstanding}}
  - Problem solving: {{student.studyFocus.problemSolving}}

## Goal
- Course: {{course.name}}
- Description: {{description}}
- Study goal: {{studyGoal}}
- Due date: {{dueDate}}
- Budget: €{{budgetEur}}
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
2. **Within budget** — stay within €{{budgetEur}} while preferring higher-rated tutors where possible.
3. **Best quality** — use the highest-rated tutors for every topic regardless of cost.

Rules:
- Only assign tutors who cover the topic AND teach in one of the student's preferred languages (fall back to all tutors if none match).
- Schedule sessions on weekdays when the tutor is available.
- Prioritise topics where the student's weak skills (low studyFocus scores) overlap with the topic's high demands.
- A student whose goal indicates minimal effort ("just pass") needs fewer sessions than one aiming for excellence — scale intensity accordingly.
- Minimise the number of distinct tutors across the plan — ideally a single tutor covers all topics; only introduce a second (or third) tutor when no single tutor covers the remaining topics.
- Do not suggest booking or transactions — this platform is for finding tutors only.
- All sessions must fall before {{dueDate}}.
- Generate one milestone per topic with a `title` and a `dueDate` spaced evenly before {{dueDate}}, ordered by topic priority.

## Output format
Return a JSON array of exactly three plan objects in this shape:
[
  {
    "tier": "cheapest" | "within_budget" | "best_quality",
    "totalEstimatedCost": <float>,
    "suggestedTutors": [
      {
        "tutorId": "<id>",
        "tutorName": "<name>"
      }
    ],
    "milestones": [
      {
        "id": "<unique id>",
        "title": "<milestone title>",
        "dueDate": "<ISO 8601 datetime>",
        "completed": false
      }
    ],
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
