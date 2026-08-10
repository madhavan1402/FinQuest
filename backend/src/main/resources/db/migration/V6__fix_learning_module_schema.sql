-- =============================================================
-- FinQuest — V6: Fix learning_modules schema
--
-- PURPOSE: Repair the Phase 4 schema mismatch so the DB columns
-- match the LearningModule JPA entity exactly.
--
-- ISSUE: V5 created the boolean column as `is_active`, but the
-- LearningModule entity field is `active` (boolean). With Spring's
-- default physical naming strategy, `active` maps to a column named
-- `active`, so Hibernate failed with:
--     Unknown column 'lm1_0.active' in 'field list'
--
-- FIX: Add the missing `active` column (tinyint(1), NOT NULL,
-- default TRUE) and populate it from the existing `is_active`
-- values so data is preserved. The existing `is_active` column is
-- left in place (safe, non-destructive).
--
-- SAFETY: This migration is idempotent against the current DB:
--   • If `active` already exists, the guard prevents adding it twice.
--   • No DROP, DELETE, TRUNCATE of any table or row.
--   • No change to module IDs, user_progress FKs, XP, coins, badges,
--     achievements or any existing data.
-- =============================================================

-- ── 1. Add the `active` column ONLY if it does not already exist ──
SET @has_active := (
    SELECT COUNT(*) > 0
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name   = 'learning_modules'
      AND column_name  = 'active'
);

SET @ddl := IF(
    @has_active = 0,
    'ALTER TABLE learning_modules
        ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 1 AFTER is_active',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ── 2. Copy existing values from is_active → active ─────────────
-- Only copies when is_active exists (guarded). This preserves the
-- current active/inactive state of every module.
SET @has_is_active := (
    SELECT COUNT(*) > 0
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name   = 'learning_modules'
      AND column_name  = 'is_active'
);

SET @upd := IF(
    @has_active = 0 AND @has_is_active = 1,
    'UPDATE learning_modules SET active = is_active',
    'SELECT 1'
);

PREPARE stmt2 FROM @upd;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;
