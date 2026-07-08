CREATE SCHEMA IF NOT EXISTS marketplace;

CREATE TABLE IF NOT EXISTS marketplace.modules (
    id uuid PRIMARY KEY,
    code varchar(64) NOT NULL UNIQUE,
    title varchar(255) NOT NULL,
    description text NOT NULL,
    difficulty_hint text NOT NULL
);

CREATE TABLE IF NOT EXISTS marketplace.module_topics (
    id uuid PRIMARY KEY,
    module_id uuid NOT NULL REFERENCES marketplace.modules(id) ON DELETE CASCADE,
    position integer NOT NULL,
    name varchar(255) NOT NULL,
    description text NOT NULL,
    difficulty_hint text NOT NULL,
    memorization integer NOT NULL CHECK (memorization BETWEEN 1 AND 5),
    formal_reasoning integer NOT NULL CHECK (formal_reasoning BETWEEN 1 AND 5),
    conceptual_understanding integer NOT NULL CHECK (conceptual_understanding BETWEEN 1 AND 5),
    problem_solving integer NOT NULL CHECK (problem_solving BETWEEN 1 AND 5),
    UNIQUE (module_id, position)
);

CREATE TABLE IF NOT EXISTS marketplace.tutor_applications (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    module_id uuid NOT NULL REFERENCES marketplace.modules(id),
    status varchar(32) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    certificate_ref varchar(512) NOT NULL,
    submitted_at timestamp with time zone NOT NULL,
    rejection_reason text
);

CREATE INDEX IF NOT EXISTS idx_tutor_applications_status_submitted_at
    ON marketplace.tutor_applications (status, submitted_at DESC);

CREATE INDEX IF NOT EXISTS idx_tutor_applications_user_id
    ON marketplace.tutor_applications (user_id);
