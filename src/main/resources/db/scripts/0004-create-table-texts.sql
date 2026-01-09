CREATE TABLE IF NOT EXISTS texts
(
    id      uuid PRIMARY KEY,
    content TEXT         NOT NULL,
    source  VARCHAR(255),
    lang    lang_t       NOT NULL,
    level   lang_level_t NOT NULL
);