CREATE TABLE student.generated_plans (
    id          uuid PRIMARY KEY,
    goal_id     uuid NOT NULL REFERENCES student.learning_goals(id) ON DELETE CASCADE,
    created_at  timestamp with time zone NOT NULL,
    UNIQUE (goal_id)
);

CREATE TABLE student.plan_suggestions (
    id                   uuid PRIMARY KEY,
    plan_id              uuid NOT NULL REFERENCES student.generated_plans(id) ON DELETE CASCADE,
    position             integer NOT NULL,
    tier                 varchar(32) NOT NULL
                             CHECK (tier IN ('CHEAPEST', 'WITHIN_BUDGET', 'BEST_QUALITY')),
    description          text NOT NULL,
    total_estimated_cost integer NOT NULL CHECK (total_estimated_cost >= 0)
);

CREATE TABLE student.plan_suggestion_tutors (
    suggestion_id uuid    NOT NULL REFERENCES student.plan_suggestions(id) ON DELETE CASCADE,
    position      integer NOT NULL,
    tutor_id      varchar(255) NOT NULL,
    display_name  varchar(255) NOT NULL,
    hourly_rate   integer NOT NULL CHECK (hourly_rate >= 0),
    PRIMARY KEY (suggestion_id, position)
);

CREATE TABLE student.plan_suggestion_milestones (
    suggestion_id  uuid    NOT NULL REFERENCES student.plan_suggestions(id) ON DELETE CASCADE,
    position       integer NOT NULL,
    title          text NOT NULL,
    due_date       timestamp with time zone NOT NULL,
    topic_id       varchar(255) NOT NULL,
    tutor_id       varchar(255) NOT NULL,
    estimated_cost integer NOT NULL CHECK (estimated_cost >= 0),
    PRIMARY KEY (suggestion_id, position)
);
