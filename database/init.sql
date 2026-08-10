<<<<<<< HEAD
-- =============================================================
-- FinQuest — database bootstrap
-- Creates the database. All table schema is managed by Flyway
-- migrations ({backend}/src/main/resources/db/migration/),
-- executed automatically when the Spring Boot app starts.
-- =============================================================

=======
-- Create the FinQuest database if it doesn't already exist
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
CREATE DATABASE IF NOT EXISTS finquest_db
    CHARACTER SET utf8mb4       -- full Unicode support (emojis, multilingual)
    COLLATE utf8mb4_unicode_ci; -- case-insensitive, accent-aware sorting

<<<<<<< HEAD
SHOW DATABASES LIKE 'finquest_db';

-- NOTE: The tables (users, refresh_tokens, learning_modules, quiz_questions,
-- quiz_results, user_progress, achievements, user_streaks, reward_wallets,
-- reward_history, xp_history, simulations) are created by the Flyway migrations:
--   backend/src/main/resources/db/migration/V1__create_normalized_schema.sql
--   backend/src/main/resources/db/migration/V2__create_refresh_tokens.sql
-- They run automatically against the database above when the app starts
-- (spring.flyway.enabled=true).

=======
-- Confirm creation
SHOW DATABASES LIKE 'finquest_db';
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
