CREATE SCHEMA IF NOT EXISTS communication;

CREATE TABLE IF NOT EXISTS communication.conversations (
    id uuid PRIMARY KEY,
    participant_a_id uuid NOT NULL,
    participant_b_id uuid NOT NULL,
    participant_a_display_name varchar(255) NOT NULL,
    participant_b_display_name varchar(255) NOT NULL,
    participant_a_tutor_id uuid,
    participant_b_tutor_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS communication.messages (
    id uuid PRIMARY KEY,
    conversation_id uuid NOT NULL REFERENCES communication.conversations(id) ON DELETE CASCADE,
    sender_id uuid NOT NULL,
    content text NOT NULL,
    sent_at timestamp with time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS messages_conversation_id_idx
    ON communication.messages (conversation_id);
