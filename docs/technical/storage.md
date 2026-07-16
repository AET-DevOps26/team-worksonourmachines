# Storage Schema

PostgreSQL is the single relational store for the platform. One shared Postgres instance runs three isolated schemas — `student`, `marketplace`, and `communication` — each owned by a dedicated database user with no cross-schema privileges. Schema isolation is enforced at the database level; services connect with credentials scoped to their own schema only.

Tables are managed by [Flyway](https://flywaydb.org/) migrations co-located with each service under `artifacts/server/<service>/src/main/resources/db/migration/`. The init script that creates the databases and users lives at `artifacts/postgres/init/db-init.sh`.

## Databases and users

| Database        | Owner user      | Created by          |
|-----------------|-----------------|---------------------|
| `student`       | `student`       | `db-init.sh`        |
| `marketplace`   | `marketplace`   | `db-init.sh`        |
| `communication` | `communication` | `db-init.sh`        |
| `keycloak`      | `keycloak`      | `db-init.sh`        |

Credentials are supplied through environment variables (`STUDENT_DB_USER`, `MARKETPLACE_DB_PASSWORD`, etc.) and fall back to the database name as default for local dev. See `.env.dist` for the full list.

---

## Schema: `student`

Managed by: `artifacts/server/student/src/main/resources/db/migration/`

Stores student identity data, learning goals, and AI-generated study plans.

### `student.student_profiles`

One row per student (keyed by the Keycloak user ID).

| Column                   | Type           | Constraints                     | Notes                                       |
|--------------------------|----------------|---------------------------------|---------------------------------------------|
| `student_id`             | `uuid`         | PK                              | Keycloak user ID                            |
| `display_name`           | `varchar(255)` | NOT NULL                        |                                             |
| `bio`                    | `text`         | NOT NULL                        |                                             |
| `memorization`           | `integer`      | CHECK (1–5), nullable           | Study-focus dimension; null until set       |
| `formal_reasoning`       | `integer`      | CHECK (1–5), nullable           | Study-focus dimension; null until set       |
| `conceptual_understanding`| `integer`     | CHECK (1–5), nullable           | Study-focus dimension; null until set       |
| `problem_solving`        | `integer`      | CHECK (1–5), nullable           | Study-focus dimension; null until set       |

All four study-focus columns are set together or left all-null. The application enforces this via `hasStudyFocus()` / `clearStudyFocus()`.

### `student.student_profile_languages`

Ordered list of preferred languages for a student.

| Column       | Type           | Constraints                                              |
|--------------|----------------|----------------------------------------------------------|
| `student_id` | `uuid`         | PK (part), FK → `student_profiles(student_id)` CASCADE  |
| `position`   | `integer`      | PK (part), 0-based order index                          |
| `language`   | `varchar(255)` | NOT NULL                                                 |

### `student.learning_goals`

A learning goal ties a student to a marketplace module they want to study.

| Column               | Type                        | Constraints                                         | Notes                                       |
|----------------------|-----------------------------|-----------------------------------------------------|---------------------------------------------|
| `id`                 | `uuid`                      | PK                                                  | Generated on persist                        |
| `student_id`         | `uuid`                      | NOT NULL, FK → `student_profiles(student_id)` CASCADE |                                           |
| `module_id`          | `varchar(255)`              | NOT NULL                                            | Code of the marketplace module              |
| `description`        | `text`                      | NOT NULL                                            | Free-text description of the goal           |
| `target_date`        | `timestamp with time zone`  | NOT NULL                                            |                                             |
| `self_assessed_level`| `integer`                   | NOT NULL, CHECK (1–5)                               | Student's self-rated current level          |
| `budget_eur`         | `integer`                   | nullable, CHECK (≥ 0)                               | Optional spending limit in EUR              |

Index: `learning_goals_student_id_idx` on `(student_id)`.

### `student.learning_goal_locations`

Preferred tutoring locations for a learning goal.

| Column     | Type          | Constraints                                         | Notes                              |
|------------|---------------|-----------------------------------------------------|------------------------------------|
| `goal_id`  | `uuid`        | PK (part), FK → `learning_goals(id)` CASCADE        |                                    |
| `position` | `integer`     | PK (part), 0-based order index                      |                                    |
| `location` | `varchar(32)` | NOT NULL, CHECK (enum)                              | `ONLINE \| GARCHING \| MUNICH \| WEIHENSTEPHAN \| STRAUBING \| OTTOBRUNN` |

### `student.generated_plans`

One AI-generated plan per learning goal (enforced by a unique constraint on `goal_id`). Replaced wholesale when the AI regenerates.

| Column       | Type                       | Constraints                                     |
|--------------|----------------------------|-------------------------------------------------|
| `id`         | `uuid`                     | PK                                              |
| `goal_id`    | `uuid`                     | NOT NULL, UNIQUE, FK → `learning_goals(id)` CASCADE |
| `created_at` | `timestamp with time zone` | NOT NULL                                        |

### `student.plan_suggestions`

Each plan contains up to three suggestions, one per pricing tier.

| Column                | Type          | Constraints                                            | Notes                                       |
|-----------------------|---------------|--------------------------------------------------------|---------------------------------------------|
| `id`                  | `uuid`        | PK                                                     |                                             |
| `plan_id`             | `uuid`        | NOT NULL, FK → `generated_plans(id)` CASCADE           |                                             |
| `position`            | `integer`     | NOT NULL                                               | Order within the plan (0-based)             |
| `tier`                | `varchar(32)` | NOT NULL, CHECK (enum)                                 | `CHEAPEST \| WITHIN_BUDGET \| BEST_QUALITY` |
| `description`         | `text`        | NOT NULL                                               |                                             |
| `total_estimated_cost`| `integer`     | NOT NULL, CHECK (≥ 0)                                  | Total cost in EUR                           |

### `student.plan_suggestion_tutors`

Snapshot of tutor info at plan-generation time (denormalized for stability).

| Column         | Type           | Constraints                                                   |
|----------------|----------------|---------------------------------------------------------------|
| `suggestion_id`| `uuid`         | PK (part), FK → `plan_suggestions(id)` CASCADE               |
| `position`     | `integer`      | PK (part)                                                     |
| `tutor_id`     | `varchar(255)` | NOT NULL                                                      |
| `display_name` | `varchar(255)` | NOT NULL                                                      |
| `hourly_rate`  | `integer`      | NOT NULL, CHECK (≥ 0)                                         |

### `student.plan_suggestion_milestones`

Ordered milestones within a plan suggestion.

| Column           | Type                       | Constraints                                             |
|------------------|----------------------------|---------------------------------------------------------|
| `suggestion_id`  | `uuid`                     | PK (part), FK → `plan_suggestions(id)` CASCADE          |
| `position`       | `integer`                  | PK (part)                                               |
| `title`          | `text`                     | NOT NULL                                                |
| `due_date`       | `timestamp with time zone` | NOT NULL                                                |
| `topic_id`       | `varchar(255)`             | NOT NULL — refers to `marketplace.module_topics.id`     |
| `tutor_id`       | `varchar(255)`             | NOT NULL — refers to `marketplace.tutor_profiles.id`    |
| `estimated_cost` | `integer`                  | NOT NULL, CHECK (≥ 0)                                   |

`topic_id` and `tutor_id` are stored as strings without a foreign key so that plan history remains readable even after marketplace data changes.

---

## Schema: `marketplace`

Managed by: `artifacts/server/marketplace/src/main/resources/db/migration/`

Stores the module catalogue and tutor data: profiles, availability, topic coverage, and tutor applications.

### `marketplace.modules`

The catalogue of academic modules tutors can offer and students can set goals for.

| Column           | Type          | Constraints              |
|------------------|---------------|--------------------------|
| `id`             | `uuid`        | PK                       |
| `code`           | `varchar(64)` | NOT NULL, UNIQUE         |
| `title`          | `varchar(255)`| NOT NULL                 |
| `description`    | `text`        | NOT NULL                 |
| `difficulty_hint`| `text`        | NOT NULL                 |

### `marketplace.module_topics`

Granular topics within a module, each with study-focus weights used for AI matching.

| Column                    | Type           | Constraints                                              | Notes                                   |
|---------------------------|----------------|----------------------------------------------------------|-----------------------------------------|
| `id`                      | `uuid`         | PK                                                       |                                         |
| `module_id`               | `uuid`         | NOT NULL, FK → `modules(id)` CASCADE                    |                                         |
| `position`                | `integer`      | NOT NULL, UNIQUE with `module_id`                        | Order within the module                 |
| `name`                    | `varchar(255)` | NOT NULL                                                 |                                         |
| `description`             | `text`         | NOT NULL                                                 |                                         |
| `difficulty_hint`         | `text`         | NOT NULL                                                 |                                         |
| `memorization`            | `integer`      | NOT NULL, CHECK (1–5)                                    | Study-focus weight                      |
| `formal_reasoning`        | `integer`      | NOT NULL, CHECK (1–5)                                    | Study-focus weight                      |
| `conceptual_understanding`| `integer`      | NOT NULL, CHECK (1–5)                                    | Study-focus weight                      |
| `problem_solving`         | `integer`      | NOT NULL, CHECK (1–5)                                    | Study-focus weight                      |

### `marketplace.tutor_profiles`

One row per approved tutor (keyed by Keycloak user ID).

| Column         | Type           | Constraints             |
|----------------|----------------|-------------------------|
| `id`           | `uuid`         | PK                      |
| `user_id`      | `uuid`         | NOT NULL, UNIQUE        |
| `display_name` | `varchar(255)` | NOT NULL                |
| `bio`          | `text`         | NOT NULL                |
| `hourly_rate`  | `real`         | NOT NULL                |
| `published`    | `boolean`      | NOT NULL, DEFAULT false |

Index: `idx_tutor_profiles_published_display_name` on `(published, display_name)` for listing queries.

### `marketplace.tutor_profile_languages`

| Column       | Type           | Constraints                                                 |
|--------------|----------------|-------------------------------------------------------------|
| `profile_id` | `uuid`         | PK (part), FK → `tutor_profiles(id)` CASCADE               |
| `position`   | `integer`      | PK (part)                                                   |
| `language`   | `varchar(128)` | NOT NULL                                                    |

### `marketplace.tutor_profile_locations`

| Column       | Type          | Constraints                                                 | Notes                              |
|--------------|---------------|-------------------------------------------------------------|------------------------------------|
| `profile_id` | `uuid`        | PK (part), FK → `tutor_profiles(id)` CASCADE               |                                    |
| `position`   | `integer`     | PK (part)                                                   |                                    |
| `location`   | `varchar(32)` | NOT NULL, CHECK (enum)                                      | Same enum as `learning_goal_locations.location` |

### `marketplace.tutor_profile_availability`

Weekly availability grid, one row per weekday per tutor profile.

| Column       | Type          | Constraints                                                 |
|--------------|---------------|-------------------------------------------------------------|
| `profile_id` | `uuid`        | PK (part), FK → `tutor_profiles(id)` CASCADE               |
| `position`   | `integer`     | PK (part)                                                   |
| `weekday`    | `varchar(32)` | NOT NULL, CHECK (enum), UNIQUE with `profile_id`            |
| `available`  | `boolean`     | NOT NULL                                                    |
| `note`       | `text`        | nullable                                                    |

Weekday enum: `MONDAY | TUESDAY | WEDNESDAY | THURSDAY | FRIDAY | SATURDAY | SUNDAY`.

### `marketplace.tutor_coverages`

Modules a tutor is qualified to teach (populated when an application is approved).

| Column              | Type          | Constraints                                              |
|---------------------|---------------|----------------------------------------------------------|
| `id`                | `uuid`        | PK                                                       |
| `profile_id`        | `uuid`        | NOT NULL, FK → `tutor_profiles(id)` CASCADE             |
| `module_id`         | `uuid`        | NOT NULL, FK → `modules(id)` CASCADE                    |
| `proficiency_level` | `varchar(64)` | NOT NULL                                                 |

Unique: `(profile_id, module_id)`. Index: `idx_tutor_coverages_module_id` on `(module_id)`.

### `marketplace.tutor_applications`

Applications from users requesting tutor status for a specific module.

| Column             | Type                       | Constraints                                          | Notes                                       |
|--------------------|----------------------------|------------------------------------------------------|---------------------------------------------|
| `id`               | `uuid`                     | PK                                                   |                                             |
| `user_id`          | `uuid`                     | NOT NULL                                             |                                             |
| `module_id`        | `uuid`                     | NOT NULL, FK → `modules(id)`                         |                                             |
| `status`           | `varchar(32)`              | NOT NULL, CHECK (enum)                               | `PENDING \| APPROVED \| REJECTED`           |
| `certificate_ref`  | `varchar(512)`             | NOT NULL                                             | Object-storage path for the certificate     |
| `submitted_at`     | `timestamp with time zone` | NOT NULL                                             |                                             |
| `rejection_reason` | `text`                     | nullable                                             | Set only when status = REJECTED             |

Indexes:
- `idx_tutor_applications_status_submitted_at` on `(status, submitted_at DESC)` — admin listing.
- `idx_tutor_applications_user_id` on `(user_id)`.
- `uq_tutor_applications_pending_user_module` — partial unique index on `(user_id, module_id)` WHERE `status = 'PENDING'` — prevents duplicate in-flight applications.

---

## Schema: `communication`

Managed by: `artifacts/server/communication/src/main/resources/db/migration/`

Stores conversations and messages between students and tutors.

### `communication.conversations`

A conversation between exactly two participants. Both Keycloak user IDs and optional tutor profile IDs are stored so the client can resolve names and navigate to profiles without a cross-service call.

| Column                       | Type                       | Constraints  | Notes                                       |
|------------------------------|----------------------------|--------------|---------------------------------------------|
| `id`                         | `uuid`                     | PK           |                                             |
| `participant_a_id`           | `uuid`                     | NOT NULL     | Keycloak user ID                            |
| `participant_b_id`           | `uuid`                     | NOT NULL     | Keycloak user ID                            |
| `participant_a_display_name` | `varchar(255)`             | NOT NULL     | Denormalized snapshot of display name       |
| `participant_b_display_name` | `varchar(255)`             | NOT NULL     | Denormalized snapshot of display name       |
| `participant_a_tutor_id`     | `uuid`                     | nullable     | Set if participant A has a tutor profile    |
| `participant_b_tutor_id`     | `uuid`                     | nullable     | Set if participant B has a tutor profile    |
| `created_at`                 | `timestamp with time zone` | NOT NULL     |                                             |
| `updated_at`                 | `timestamp with time zone` | NOT NULL     | Bumped on each new message                  |

Display names are snapshotted at conversation creation and updated via event from other services when they change.

### `communication.messages`

Individual messages within a conversation.

| Column            | Type                       | Constraints                                               |
|-------------------|----------------------------|-----------------------------------------------------------|
| `id`              | `uuid`                     | PK                                                        |
| `conversation_id` | `uuid`                     | NOT NULL, FK → `conversations(id)` CASCADE               |
| `sender_id`       | `uuid`                     | NOT NULL — Keycloak user ID of the sender                 |
| `content`         | `text`                     | NOT NULL                                                  |
| `sent_at`         | `timestamp with time zone` | NOT NULL                                                  |

Index: `messages_conversation_id_idx` on `(conversation_id)`.

---

## Cross-cutting conventions

- **Primary keys** are `uuid`, generated by the application on first persist (`@PrePersist`) rather than by the database, so IDs are known before the insert round-trip.
- **Timestamps** use `timestamp with time zone` (`OffsetDateTime` in Java) throughout. Times are stored in UTC.
- **Ordered collections** (languages, locations, availability, tutors, milestones) use an integer `position` column as the order key rather than relying on insertion order.
- **Enums** are stored as `varchar` with a `CHECK` constraint. The application maps them with `@Enumerated(EnumType.STRING)`.
- **Cross-schema references** (e.g. `plan_suggestion_milestones.topic_id` → marketplace) are stored as plain `varchar`/`uuid` strings without a database foreign key, preserving read access to historical data after upstream records are modified or deleted.
- **Migrations** follow Flyway's `V{n}__{description}.sql` naming convention. Each service owns only its own schema's migrations and runs them independently on startup.
