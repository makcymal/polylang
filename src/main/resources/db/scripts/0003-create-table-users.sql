CREATE TABLE IF NOT EXISTS users
(
    id                     uuid PRIMARY KEY,
    email                  VARCHAR(255) UNIQUE      NOT NULL,
    email_confirmed        BOOLEAN                  NOT NULL DEFAULT FALSE,
    username               VARCHAR(64) UNIQUE       NOT NULL,
    password_hash          VARCHAR(60)              NOT NULL,
    native_lang            lang_t,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    last_authenticated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    last_reset_password_at TIMESTAMP WITH TIME ZONE
);