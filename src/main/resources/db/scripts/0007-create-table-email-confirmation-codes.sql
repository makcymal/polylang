CREATE TABLE IF NOT EXISTS email_confirmation_codes
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(255) UNIQUE      NOT NULL,
    code       VARCHAR(6)               NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);