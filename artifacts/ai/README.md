# AI Component — Inputs & Outputs

The AI component is a FastAPI service that wraps a configurable LLM (Ollama, LM Studio, OpenAI, or Logos) and exposes two endpoints: a generic `/v1/chat` endpoint and a `/v1/plan` endpoint for generating personalised study plans.

---

## Plan Generation (`POST /v1/plan`)

### Inputs

| Field | Type | Description |
|---|---|---|
| `mode` | `"standard" \| "cost_optimized"` | Controls overall optimisation strategy |
| `student.id` | string | Unique student identifier |
| `student.name` | string | Display name |
| `student.preferredLanguages` | string[] | e.g. `["en", "de"]` — used to filter tutors |
| `course.id` | string | Course / module identifier |
| `course.name` | string | Human-readable course name (e.g. "Linear Algebra") |
| `course.topics` | `Topic[]` | Ordered list of topics with difficulty levels |
| `milestone.id` | string | Goal identifier (e.g. exam session) |
| `milestone.name` | string | Human-readable goal name |
| `milestone.dueDate` | ISO 8601 datetime | Deadline — the plan fits sessions before this date |
| `milestone.completed` | boolean | Whether the milestone is already done |
| `tutors` | `Tutor[]` | Available tutors with rates, languages, topic coverage, and per-day availability slots |

#### `Topic` shape
```json
{ "id": "t1", "name": "Matrix Decomposition", "difficultyLevel": 3 }
```
`difficultyLevel` is 1–5 (1 = easy, 5 = very hard).

#### `Tutor` shape
```json
{
  "id": "u1",
  "name": "Anna Schmidt",
  "hourlyRate": 18.00,
  "languages": ["de", "en"],
  "topicIds": ["t1", "t2"],
  "availability": [
    { "date": "2026-07-01", "slots": ["10:00", "14:00", "16:00"] }
  ]
}
```

#### Budget
The budget is not a top-level field in the current API contract — it is **derived** from the student context and the number of sessions the AI schedules. To enforce a hard budget cap, the caller should pass `mode: "cost_optimized"` and filter the tutor list to those whose `hourlyRate` fits.

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
| `schedule` | `ScheduleEntry[]` | Ordered list of booked sessions |
| `totalEstimatedCost` | float | Sum of all session costs in EUR |

#### `ScheduleEntry` shape
```json
{
  "date": "2026-07-03",
  "topicId": "t1",
  "topicName": "Matrix Decomposition",
  "tutorId": "u1",
  "tutorName": "Anna Schmidt",
  "estimatedCost": 18.00
}
```

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
