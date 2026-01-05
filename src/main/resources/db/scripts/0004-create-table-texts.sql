CREATE TABLE IF NOT EXISTS texts
(
    id             uuid PRIMARY KEY,
    content        TEXT       NOT NULL,
    language       language_t NOT NULL,
    intended_level language_level_t
);