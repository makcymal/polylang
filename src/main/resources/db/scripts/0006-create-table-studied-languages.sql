CREATE TABLE IF NOT EXISTS studied_languages
(
    id              SERIAL PRIMARY KEY,
    user_id         INT        NOT NULL REFERENCES users (id),
    language        language_t NOT NULL,
    declared_level  language_level_t,
    estimated_level language_level_t
);