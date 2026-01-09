CREATE TABLE IF NOT EXISTS stats
(
    id      uuid PRIMARY KEY,
    user_id uuid   NOT NULL REFERENCES users (id),
    lang    lang_t NOT NULL,
    history jsonb  NOT NULL
);