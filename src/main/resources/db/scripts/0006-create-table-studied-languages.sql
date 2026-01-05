CREATE TABLE IF NOT EXISTS studied_languages
(
    id              uuid PRIMARY KEY,
    user_id         uuid       NOT NULL REFERENCES users (id),
    language        language_t NOT NULL,
    declared_level  language_level_t,
    estimated_level language_level_t
);