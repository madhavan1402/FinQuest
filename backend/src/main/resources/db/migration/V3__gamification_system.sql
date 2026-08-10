-- =============================================================
-- FinQuest — V3: Gamification system
-- Achievement definitions, user_achievements, user_badges
-- =============================================================

-- ── achievement_definitions ──────────────────────────────────
-- Master list of all possible achievements (seeded by app).
CREATE TABLE achievement_definitions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon        VARCHAR(10)  NOT NULL DEFAULT '🏅',
    xp_reward   INT NOT NULL DEFAULT 0,
    coin_reward INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_ach_def_code UNIQUE (code)
);

-- ── user_achievements ─────────────────────────────────────────
-- Records which users have unlocked which achievements.
-- One row per (user, achievement_definition) — enforced by unique constraint.
CREATE TABLE user_achievements (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                   BIGINT NOT NULL,
    achievement_definition_id BIGINT NOT NULL,
    unlocked_at               DATETIME(6) NOT NULL,
    CONSTRAINT fk_ua_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ua_def  FOREIGN KEY (achievement_definition_id) REFERENCES achievement_definitions(id),
    CONSTRAINT uk_ua_user_def UNIQUE (user_id, achievement_definition_id)
);

-- ── user_badges ───────────────────────────────────────────────
-- Badges are the visual representation of achievements.
-- One row per (user, badge_code) — enforced by unique constraint.
CREATE TABLE user_badges (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    badge_code  VARCHAR(50)  NOT NULL,
    badge_name  VARCHAR(100) NOT NULL,
    icon        VARCHAR(10)  NOT NULL DEFAULT '🏅',
    unlocked_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_ub_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_ub_user_code UNIQUE (user_id, badge_code)
);

-- ── Seed achievement definitions ─────────────────────────────
INSERT INTO achievement_definitions (code, name, description, icon, xp_reward, coin_reward) VALUES
('FIRST_QUIZ',           'First Quiz',          'Complete your first quiz',                    '📝', 50,  10),
('QUIZ_MASTER',          'Quiz Master',         'Pass 10 quizzes',                             '🥇', 200, 50),
('PERFECT_SCORE',        'Perfect Score',       'Get 100% on any quiz',                        '💯', 150, 40),
('FIRST_LEVEL_COMPLETE', 'Level Complete',      'Complete your first learning level',          '🎓', 100, 25),
('FIVE_DAY_STREAK',      '5-Day Streak',        'Maintain a 5-day learning streak',            '🔥', 100, 30),
('TEN_DAY_STREAK',       '10-Day Streak',       'Maintain a 10-day learning streak',           '🔥', 250, 75),
('THIRTY_DAY_STREAK',    '30-Day Streak',       'Maintain a 30-day learning streak',           '🔥', 500, 150),
('FIRST_SIMULATION',     'Simulator',           'Run your first financial simulation',         '📊', 75,  20),
('SAVING_MASTER',        'Saving Master',       'Complete the Saving Money module',            '💰', 150, 40),
('INVESTMENT_BEGINNER',  'Investment Beginner', 'Complete the Investing Basics module',        '📈', 150, 40),
('FINANCE_EXPLORER',     'Finance Explorer',    'Complete 5 learning modules',                 '🏆', 200, 50),
('XP_1000',              'XP Milestone 1000',   'Earn 1000 total XP',                          '⭐', 100, 25),
('XP_5000',              'XP Milestone 5000',   'Earn 5000 total XP',                          '🌟', 300, 100),
('LEVEL_10',             'Level 10',            'Reach Level 10',                              '🚀', 500, 150);

-- ── Indexes ───────────────────────────────────────────────────
CREATE INDEX idx_ua_user ON user_achievements(user_id);
CREATE INDEX idx_ub_user ON user_badges(user_id);
