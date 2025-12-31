CREATE TABLE IF NOT EXISTS refresh_tokens
(
    jti        uuid PRIMARY KEY,
    access_jti uuid                     NOT NULL,
    user_id    uuid                     NOT NULL REFERENCES users (id),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);