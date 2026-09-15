CREATE TABLE tb_users (
    id UUID PRIMARY KEY,

    name VARCHAR(120) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    role VARCHAR(50) NOT NULL
);

CREATE TABLE tb_refresh_tokens (
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    token_hash VARCHAR(255) NOT NULL UNIQUE,

    expires_at TIMESTAMP NOT NULL,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES tb_users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id
    ON tb_refresh_tokens(user_id);

CREATE INDEX idx_refresh_tokens_expires_at
    ON tb_refresh_tokens(expires_at);

