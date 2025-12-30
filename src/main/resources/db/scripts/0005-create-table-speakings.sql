CREATE TABLE IF NOT EXISTS speaking
(
    id            SERIAL PRIMARY KEY,
    user_id       INT NOT NULL REFERENCES users (id),
    text_id       INT NOT NULL REFERENCES texts (id),
    transcription TEXT,
    analysis      TEXT,
    score         INT,
    created_at    TIMESTAMP WITH TIME ZONE
);