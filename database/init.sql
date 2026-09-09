-- =============================================================
-- FinQuest — database bootstrap
-- Creates the database. All table schema is managed by Flyway
-- migrations ({backend}/src/main/resources/db/migration/),
-- executed automatically when the Spring Boot app starts.
-- =============================================================

CREATE DATABASE IF NOT EXISTS finquest_db
    CHARACTER SET utf8mb4       -- full Unicode support (emojis, multilingual)
    COLLATE utf8mb4_unicode_ci; -- case-insensitive, accent-aware sorting

SHOW DATABASES LIKE 'finquest_db';

-- NOTE: The tables (users, refresh_tokens, learning_modules, quiz_questions,
-- quiz_results, user_progress, achievements, user_streaks, reward_wallets,
-- reward_history, xp_history, simulations) are created by the Flyway migrations:
--   backend/src/main/resources/db/migration/V1__create_normalized_schema.sql
--   backend/src/main/resources/db/migration/V2__create_refresh_tokens.sql
-- They run automatically against the database above when the app starts
-- (spring.flyway.enabled=true).
