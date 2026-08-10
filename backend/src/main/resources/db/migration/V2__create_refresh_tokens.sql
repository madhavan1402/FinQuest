-- =============================================================
-- FinQuest — V2: Refresh tokens
-- One row per issued refresh token, so tokens can be individually
-- revoked (secure logout) and rotated on each refresh.
-- Tokens are stored SHA-256 hashed, never in plain text.
-- =============================================================

CREATE TABLE refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_hash  VARCHAR(128) NOT NULL,
    user_id     BIGINT NOT NULL,
    expires_at  DATETIME(6) NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_refresh_hash UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_user      ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_expires   ON refresh_tokens(expires_at);

