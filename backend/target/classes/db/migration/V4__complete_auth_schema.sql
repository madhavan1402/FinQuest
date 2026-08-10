-- =============================================================
-- FinQuest — V4: Complete auth schema
-- Adds the 6 authentication columns that were missing because
-- the database was baselined before V1 ran.
--
-- Safety rules:
--   • ALTER TABLE only — no DROP, no TRUNCATE, no DELETE
--   • All existing rows (Madhavan, Manimaran, Karthikeyan) are preserved
--   • XP, coins, level, progress, achievements, badges untouched
--   • Existing users get email_verified = TRUE so they can log in
--     (they predate email verification; new registrations start FALSE)
-- =============================================================

ALTER TABLE users
    ADD COLUMN role               VARCHAR(20)  NOT NULL DEFAULT 'USER'  AFTER password,
    ADD COLUMN email_verified     BOOLEAN      NOT NULL DEFAULT FALSE    AFTER role,
    ADD COLUMN verification_code  VARCHAR(64)  NULL                      AFTER email_verified,
    ADD COLUMN verification_expiry DATETIME(6) NULL                      AFTER verification_code,
    ADD COLUMN reset_code         VARCHAR(64)  NULL                      AFTER verification_expiry,
    ADD COLUMN reset_expiry       DATETIME(6)  NULL                      AFTER reset_code;

-- Existing users predate email verification — mark them as verified
-- so they are not locked out. New registrations start with FALSE (the default).
UPDATE users SET email_verified = TRUE WHERE email_verified = FALSE;

-- Add index on reset_code for the forgot-password lookup
-- (UserRepository.findByResetCode uses this column)
CREATE INDEX idx_users_reset_code ON users(reset_code);
