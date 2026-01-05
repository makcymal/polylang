CREATE TABLE IF NOT EXISTS talks
(
    id            uuid PRIMARY KEY,
    user_id       uuid NOT NULL REFERENCES users (id),
    text_id       uuid NOT NULL REFERENCES texts (id),
    transcription TEXT,
    analysis      TEXT,
    score         INT,
    created_at    TIMESTAMP WITH TIME ZONE
);