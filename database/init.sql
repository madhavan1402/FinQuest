-- Create the FinQuest database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS finquest_db
    CHARACTER SET utf8mb4       -- full Unicode support (emojis, multilingual)
    COLLATE utf8mb4_unicode_ci; -- case-insensitive, accent-aware sorting

-- Confirm creation
SHOW DATABASES LIKE 'finquest_db';
