CREATE SCHEMA IF NOT EXISTS student;

CREATE TABLE IF NOT EXISTS student.student_profiles (
    student_id uuid PRIMARY KEY,
    display_name varchar(255) NOT NULL,
    bio text NOT NULL,
    memorization integer CHECK (memorization BETWEEN 1 AND 5),
    formal_reasoning integer CHECK (formal_reasoning BETWEEN 1 AND 5),
    conceptual_understanding integer CHECK (conceptual_understanding BETWEEN 1 AND 5),
    problem_solving integer CHECK (problem_solving BETWEEN 1 AND 5)
);

CREATE TABLE IF NOT EXISTS student.student_profile_languages (
    student_id uuid NOT NULL REFERENCES student.student_profiles(student_id) ON DELETE CASCADE,
    position integer NOT NULL,
    language varchar(255) NOT NULL,
    PRIMARY KEY (student_id, position)
);

CREATE TABLE IF NOT EXISTS student.learning_goals (
    id uuid PRIMARY KEY,
    student_id uuid NOT NULL REFERENCES student.student_profiles(student_id) ON DELETE CASCADE,
    module_id varchar(255) NOT NULL,
    description text NOT NULL,
    target_date timestamp with time zone NOT NULL,
    self_assessed_level integer NOT NULL CHECK (self_assessed_level BETWEEN 1 AND 5),
    budget_eur integer CHECK (budget_eur >= 0)
);

CREATE INDEX IF NOT EXISTS learning_goals_student_id_idx
    ON student.learning_goals (student_id);

CREATE TABLE IF NOT EXISTS student.generated_plans (
    id          uuid PRIMARY KEY,
    goal_id     uuid NOT NULL REFERENCES student.learning_goals(id) ON DELETE CASCADE,
    created_at  timestamp with time zone NOT NULL,
    UNIQUE (goal_id)
);

CREATE TABLE IF NOT EXISTS student.plan_suggestions (
    id                   uuid PRIMARY KEY,
    plan_id              uuid NOT NULL REFERENCES student.generated_plans(id) ON DELETE CASCADE,
    position             integer NOT NULL,
    tier                 varchar(32) NOT NULL
                             CHECK (tier IN ('CHEAPEST', 'WITHIN_BUDGET', 'BEST_QUALITY')),
    description          text NOT NULL,
    total_estimated_cost integer NOT NULL CHECK (total_estimated_cost >= 0)
);

CREATE TABLE IF NOT EXISTS student.plan_suggestion_tutors (
    suggestion_id uuid    NOT NULL REFERENCES student.plan_suggestions(id) ON DELETE CASCADE,
    position      integer NOT NULL,
    tutor_id      varchar(255) NOT NULL,
    display_name  varchar(255) NOT NULL,
    hourly_rate   integer NOT NULL CHECK (hourly_rate >= 0),
    PRIMARY KEY (suggestion_id, position)
);

CREATE TABLE IF NOT EXISTS student.plan_suggestion_milestones (
    suggestion_id  uuid    NOT NULL REFERENCES student.plan_suggestions(id) ON DELETE CASCADE,
    position       integer NOT NULL,
    title          text NOT NULL,
    due_date       timestamp with time zone NOT NULL,
    topic_id       varchar(255) NOT NULL,
    tutor_id       varchar(255) NOT NULL,
    estimated_cost integer NOT NULL CHECK (estimated_cost >= 0),
    PRIMARY KEY (suggestion_id, position)
);

CREATE TABLE IF NOT EXISTS student.learning_goal_locations (
    goal_id uuid NOT NULL REFERENCES student.learning_goals(id) ON DELETE CASCADE,
    position integer NOT NULL,
    location varchar(32) NOT NULL CHECK (
        location IN ('ONLINE', 'GARCHING', 'MUNICH', 'WEIHENSTEPHAN', 'STRAUBING', 'OTTOBRUNN')
    ),
    PRIMARY KEY (goal_id, position)
);

INSERT INTO student.student_profiles (
    student_id,
    display_name,
    bio,
    memorization,
    formal_reasoning,
    conceptual_understanding,
    problem_solving
)
VALUES
    (
        '11111111-1111-1111-1111-111111111101',
        'Lukas Weber',
        'Lukas is a student interested in improving his learning skills and building stronger study habits.',
        3,
        4,
        4,
        3
    ),
    (
        '11111111-1111-1111-1111-111111111102',
        'Maria Schneider',
        'Maria is a motivated student who wants to strengthen her conceptual understanding and problem-solving abilities.',
        4,
        3,
        5,
        4
    ),
    (
        '11111111-1111-1111-1111-111111111103',
        'Jonas Fischer',
        'Jonas is a student focused on developing better reasoning skills and becoming more confident with complex topics.',
        3,
        5,
        3,
        4
    )
ON CONFLICT (student_id) DO UPDATE
    SET
        display_name = EXCLUDED.display_name,
        bio = EXCLUDED.bio,
        memorization = EXCLUDED.memorization,
        formal_reasoning = EXCLUDED.formal_reasoning,
        conceptual_understanding = EXCLUDED.conceptual_understanding,
        problem_solving = EXCLUDED.problem_solving;
