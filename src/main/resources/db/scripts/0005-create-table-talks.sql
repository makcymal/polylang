CREATE TABLE IF NOT EXISTS talks
(
    id            uuid PRIMARY KEY,
    user_id       uuid                     NOT NULL REFERENCES users (id),
    text_id       uuid                     NOT NULL REFERENCES texts (id),
    client_id     uuid                     NOT NULL,
    transcription TEXT,
    analysis      TEXT,
    score         REAL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE
);