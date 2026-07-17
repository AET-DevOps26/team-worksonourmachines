-- Prefer the oldest conversation for each unordered participant pair.
DELETE FROM communication.conversations c
WHERE EXISTS (
    SELECT 1
    FROM communication.conversations older
    WHERE LEAST(older.participant_a_id, older.participant_b_id)
            = LEAST(c.participant_a_id, c.participant_b_id)
      AND GREATEST(older.participant_a_id, older.participant_b_id)
            = GREATEST(c.participant_a_id, c.participant_b_id)
      AND (
            older.created_at < c.created_at
            OR (older.created_at = c.created_at AND older.id < c.id)
          )
);

CREATE UNIQUE INDEX conversations_participants_unique
    ON communication.conversations (
        LEAST(participant_a_id, participant_b_id),
        GREATEST(participant_a_id, participant_b_id)
    );
