CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL,
    email_confirmed BOOLEAN             NOT NULL DEFAULT FALSE,
    username        VARCHAR(255) UNIQUE NOT NULL,
    password_salt   uuid                NOT NULL,
    password_hash   VARCHAR(60)         NOT NULL,
    native_language language_t
);