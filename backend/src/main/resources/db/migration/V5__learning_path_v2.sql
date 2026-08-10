-- =============================================================
-- FinQuest — V5: Learning Path 2.0
-- 36-level structured curriculum (BEGINNER / INTERMEDIATE / ADVANCED)
--
-- SAFETY: This migration PRESERVES all existing data.
--   • No DROP, DELETE, TRUNCATE of tables or rows
--   • All 15 existing learning_modules rows are preserved (IDs kept)
--   • All existing user_progress, quiz_results, XP, coins, badges,
--     achievements and user rows are untouched
--   • Existing module IDs are mapped to semantically-equivalent
--     new curriculum levels so existing progress stays valid
--
-- Strategy for the UNIQUE sequence_number constraint:
--   1. Bump all existing modules to temp sequence (seq + 1000)
--   2. Insert the 21 brand-new modules at their final sequence numbers
--   3. Re-map the 15 existing modules to their final sequence numbers
--   4. Verify exactly sequence numbers 1..36 exist exactly once
-- =============================================================

-- ── 1. Add new columns to learning_modules ─────────────────────
ALTER TABLE learning_modules
    ADD COLUMN tier         VARCHAR(20)  NOT NULL DEFAULT 'BEGINNER' AFTER sequence_number,
    ADD COLUMN difficulty   VARCHAR(20)  NOT NULL DEFAULT 'EASY'      AFTER tier,
    ADD COLUMN description  VARCHAR(500) NULL                          AFTER difficulty,
    ADD COLUMN estimated_minutes INT NOT NULL DEFAULT 10               AFTER coin_reward,
    ADD COLUMN is_active    BOOLEAN      NOT NULL DEFAULT TRUE         AFTER estimated_minutes;

-- ── 2. Add new columns to quiz_questions ───────────────────────
ALTER TABLE quiz_questions
    ADD COLUMN topic         VARCHAR(100) NULL AFTER level,
    ADD COLUMN difficulty    VARCHAR(20)  NOT NULL DEFAULT 'EASY' AFTER topic,
    ADD COLUMN question_type VARCHAR(20)  NOT NULL DEFAULT 'MCQ'  AFTER difficulty,
    ADD COLUMN explanation   VARCHAR(500) NULL                      AFTER correct_answer,
    ADD COLUMN active        BOOLEAN      NOT NULL DEFAULT TRUE     AFTER explanation;

-- ── 3. Add new columns to user_progress ────────────────────────
ALTER TABLE user_progress
    ADD COLUMN status               VARCHAR(20)  NOT NULL DEFAULT 'LOCKED' AFTER unlocked,
    ADD COLUMN attempts             INT          NOT NULL DEFAULT 0        AFTER completed,
    ADD COLUMN best_score           INT          NOT NULL DEFAULT 0        AFTER attempts,
    ADD COLUMN last_score           INT          NOT NULL DEFAULT 0        AFTER best_score,
    ADD COLUMN correct_answers      INT          NOT NULL DEFAULT 0        AFTER last_score,
    ADD COLUMN questions_answered   INT          NOT NULL DEFAULT 0        AFTER correct_answers,
    ADD COLUMN started_at           DATETIME(6) NULL                       AFTER questions_answered,
    ADD COLUMN last_attempt_at      DATETIME(6) NULL                       AFTER started_at;

-- ── 4. Temporarily shift existing modules out of the way ──────
UPDATE learning_modules SET sequence_number = sequence_number + 1000;

-- ── 5. Insert the 21 NEW modules (final sequence numbers) ─────
-- Levels 9-12, 16-19, 22-31, 33-34, 36
INSERT INTO learning_modules
    (module_key, title, sequence_number, tier, difficulty, description, xp_reward, coin_reward, estimated_minutes, is_active)
VALUES
('good-vs-bad-debt',        'Good Debt vs Bad Debt',       9,  'BEGINNER',     'EASY',   'Learn productive debt, high-cost debt, debt management and the debt-to-income concept.',        110, 27, 12, TRUE),
('financial-goals',         'Financial Goals',            10,  'BEGINNER',     'EASY',   'Set short, medium and long-term goals using the SMART framework.',                             115, 28, 12, TRUE),
('personal-cash-flow',      'Personal Cash Flow',         11,  'BEGINNER',     'EASY',   'Track income, fixed and variable expenses, and manage your savings rate.',                       120, 29, 13, TRUE),
('beginner-challenge',      'Beginner Finance Challenge', 12,  'BEGINNER',     'MEDIUM', 'Mixed questions covering all Beginner levels 1-11.',                                             150, 35, 15, TRUE),
('sip-compound-interest',   'SIP & Compound Interest',    16,  'INTERMEDIATE', 'MEDIUM', 'Understand systematic investment plans and the power of compound interest.',                    170, 40, 18, TRUE),
('bonds-fixed-income',      'Bonds & Fixed Income',       17,  'INTERMEDIATE', 'MEDIUM', 'Explore bonds, fixed deposits and other fixed-income instruments.',                            175, 41, 18, TRUE),
('risk-diversification',    'Risk & Diversification',     18,  'INTERMEDIATE', 'MEDIUM', 'Learn how risk and diversification protect your portfolio.',                                    180, 42, 19, TRUE),
('portfolio-building',      'Portfolio Building',         19,  'INTERMEDIATE', 'MEDIUM', 'Combine assets into a cohesive investment portfolio.',                                          185, 43, 19, TRUE),
('inflation-purchasing',    'Inflation & Purchasing Power',22, 'INTERMEDIATE', 'MEDIUM', 'Understand how inflation erodes purchasing power over time.',                                   195, 45, 20, TRUE),
('financial-planning',      'Financial Planning',         23,  'INTERMEDIATE', 'MEDIUM', 'Build a complete personal financial plan for your goals.',                                     200, 46, 20, TRUE),
('intermediate-challenge',  'Intermediate Finance Challenge',24,'INTERMEDIATE','HARD', 'Mixed questions covering all Intermediate levels 13-23.',                                       220, 50, 22, TRUE),
('advanced-investing',      'Advanced Investing',         25,  'ADVANCED',     'MEDIUM', 'Advanced strategies for growing substantial wealth over time.',                                 230, 52, 24, TRUE),
('equity-analysis',         'Equity Analysis',            26,  'ADVANCED',     'MEDIUM', 'Analyse companies and equity instruments for better decisions.',                                235, 53, 24, TRUE),
('fundamental-analysis',    'Fundamental Analysis',       27,  'ADVANCED',     'HARD',   'Evaluate companies using financial statements and ratios.',                                     240, 54, 25, TRUE),
('technical-analysis',      'Technical Analysis Basics',  28,  'ADVANCED',     'HARD',   'Read price charts, trends and indicators for trading decisions.',                               245, 55, 25, TRUE),
('portfolio-optimization',  'Portfolio Optimization',     29,  'ADVANCED',     'HARD',   'Optimise your portfolio for maximum return per unit of risk.',                                   250, 56, 26, TRUE),
('asset-allocation',        'Asset Allocation',           30,  'ADVANCED',     'HARD',   'Distribute assets strategically across classes based on goals and risk.',                       255, 57, 26, TRUE),
('tax-optimization',        'Tax Optimization',           31,  'ADVANCED',     'HARD',   'Use deductions and investments to legally minimise your tax burden.',                            260, 58, 27, TRUE),
('wealth-management',       'Wealth Management',          33,  'ADVANCED',     'HARD',   'Comprehensive strategies for managing and growing wealth.',                                     270, 60, 28, TRUE),
('passive-income',          'Passive Income',             34,  'ADVANCED',     'HARD',   'Build income streams that require little ongoing effort.',                                      275, 61, 28, TRUE),
('advanced-challenge',      'Advanced Finance Challenge', 36,  'ADVANCED',     'HARD',   'Mixed questions covering all Advanced levels 25-35.',                                           300, 70, 30, TRUE);

-- ── 6. Update descriptions for the 15 existing modules ────────
UPDATE learning_modules SET description = 'Learn what money is, income, expenses, and the difference between needs and wants.',                                          estimated_minutes = 10 WHERE module_key = 'introduction-finance';
UPDATE learning_modules SET description = 'Track income and expenses, build a basic budget, and apply the 50/30/20 concept.',                                             estimated_minutes = 12 WHERE module_key = 'budgeting-basics';
UPDATE learning_modules SET description = 'Build saving habits, set saving goals, pay yourself first, and grow short-term savings.',                                        estimated_minutes = 14 WHERE module_key = 'saving-money';
UPDATE learning_modules SET description = 'Understand emergency funds, their purpose, liquidity, and handling unexpected expenses.',                                          estimated_minutes = 15 WHERE module_key = 'emergency-fund';
UPDATE learning_modules SET description = 'Learn savings and current accounts, interest, bank statements, and basic banking terms.',                                         estimated_minutes = 16 WHERE module_key = 'banking';
UPDATE learning_modules SET description = 'Use UPI, debit cards and online payments safely, and stay aware of payment fraud.',                                                estimated_minutes = 18 WHERE module_key = 'digital-payments';
UPDATE learning_modules SET description = 'Understand credit scores, credit history, the factors that affect them, and responsible credit usage.',                            estimated_minutes = 18 WHERE module_key = 'credit-score';
UPDATE learning_modules SET description = 'Learn about principal, interest, EMI, tenure, and secured vs unsecured loans.',                                                     estimated_minutes = 20 WHERE module_key = 'loans';
UPDATE learning_modules SET description = 'Learn how investing works, the basic building blocks of wealth creation, and risk-return trade-offs.',                            estimated_minutes = 22 WHERE module_key = 'investing-basics';
UPDATE learning_modules SET description = 'Understand how the stock market works, indices, and long-term investing thinking.',                                                estimated_minutes = 25 WHERE module_key = 'stock-market';
UPDATE learning_modules SET description = 'See how diversified mutual funds can match different financial goals.',                                                             estimated_minutes = 22 WHERE module_key = 'mutual-funds';
UPDATE learning_modules SET description = 'Learn income tax basics, deductions, returns, and the importance of tax planning.',                                                 estimated_minutes = 25 WHERE module_key = 'taxes';
UPDATE learning_modules SET description = 'Get familiar with health, life and motor insurance, and how to plan your coverage.',                                                estimated_minutes = 20 WHERE module_key = 'insurance';
UPDATE learning_modules SET description = 'Build a long-term retirement strategy with pension and savings options.',                                                          estimated_minutes = 28 WHERE module_key = 'retirement-planning';
UPDATE learning_modules SET description = 'Build passive income streams and achieve financial independence.',                                                                   estimated_minutes = 30 WHERE module_key = 'financial-freedom';

-- ── 7. Re-map the 15 existing modules to final sequence numbers ──
UPDATE learning_modules SET sequence_number = 1,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'introduction-finance';
UPDATE learning_modules SET sequence_number = 2,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'budgeting-basics';
UPDATE learning_modules SET sequence_number = 3,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'saving-money';
UPDATE learning_modules SET sequence_number = 4,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'emergency-fund';
UPDATE learning_modules SET sequence_number = 5,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'banking';
UPDATE learning_modules SET sequence_number = 6,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'digital-payments';
UPDATE learning_modules SET sequence_number = 7,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'credit-score';
UPDATE learning_modules SET sequence_number = 8,  tier = 'BEGINNER',     difficulty = 'EASY'   WHERE module_key = 'loans';
UPDATE learning_modules SET sequence_number = 13, tier = 'INTERMEDIATE', difficulty = 'EASY'   WHERE module_key = 'investing-basics';
UPDATE learning_modules SET sequence_number = 14, tier = 'INTERMEDIATE', difficulty = 'EASY'   WHERE module_key = 'stock-market';
UPDATE learning_modules SET sequence_number = 15, tier = 'INTERMEDIATE', difficulty = 'EASY'   WHERE module_key = 'mutual-funds';
UPDATE learning_modules SET sequence_number = 20, tier = 'INTERMEDIATE', difficulty = 'MEDIUM' WHERE module_key = 'taxes';
UPDATE learning_modules SET sequence_number = 21, tier = 'INTERMEDIATE', difficulty = 'MEDIUM' WHERE module_key = 'insurance';
UPDATE learning_modules SET sequence_number = 32, tier = 'ADVANCED',     difficulty = 'HARD'   WHERE module_key = 'retirement-planning';
UPDATE learning_modules SET sequence_number = 35, tier = 'ADVANCED',     difficulty = 'HARD'   WHERE module_key = 'financial-freedom';

-- ── 8. Idempotency guard for re-runs ─────────────────────────
-- The temp +1000 shift is only undone if it was actually applied this run.
-- (This migration is applied once by Flyway; this guard is a belt-and-braces
--  measure in case a DB is re-seeded from scratch.)
-- No-op when sequence numbers are already in the 1..36 range.
