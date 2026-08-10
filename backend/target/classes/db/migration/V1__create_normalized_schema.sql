-- =============================================================
-- FinQuest — V1: Core normalized schema
-- Auth + gamification tables. Roles are stored inline on users
-- (USER/ADMIN enum string) — no separate role table is needed.
-- =============================================================

-- ── users ────────────────────────────────────────────────────
-- Central account table combining identity + gamification state.
-- Normalised so authentication fields and player statistics live
-- on the same row as the account (they are 1:1), while 1:N and
-- N:M data lives in dedicated child tables below.
CREATE TABLE users (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                   VARCHAR(100)  NOT NULL,
    email                  VARCHAR(150)  NOT NULL,
    password               VARCHAR(255)  NOT NULL,
    role                   VARCHAR(20)   NOT NULL DEFAULT 'USER',
    email_verified         BOOLEAN       NOT NULL DEFAULT FALSE,
    verification_code      VARCHAR(64),
    verification_expiry    DATETIME(6),
    reset_code             VARCHAR(64),
    reset_expiry           DATETIME(6),
    xp                     INT DEFAULT 0 NOT NULL,
    level                  INT DEFAULT 1 NOT NULL,
    financial_score        INT DEFAULT 0 NOT NULL,
    coins                  INT DEFAULT 0 NOT NULL,
    learning_streak        INT DEFAULT 0 NOT NULL,
    last_learning_date     DATE,
    literacy_level         VARCHAR(20)   DEFAULT 'Beginner',
    recommendation         VARCHAR(300)  DEFAULT 'Complete your first quiz to get started',
    risk_profile           VARCHAR(20)   DEFAULT 'Moderate',
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- ── learning_modules ─────────────────────────────────────────
CREATE TABLE learning_modules (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_key       VARCHAR(255) NOT NULL,
    title            VARCHAR(255) NOT NULL,
    sequence_number  INT NOT NULL,
    xp_reward        INT NOT NULL,
    coin_reward      INT NOT NULL DEFAULT 25,
    CONSTRAINT uk_module_key UNIQUE (module_key),
    CONSTRAINT uk_module_seq UNIQUE (sequence_number)
);

-- ── user_progress ────────────────────────────────────────────
-- 1:N — each user has one row per module they've reached.
CREATE TABLE user_progress (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    learning_module_id  BIGINT NOT NULL,
    unlocked            BOOLEAN NOT NULL,
    completed           BOOLEAN NOT NULL,
    completed_at        DATETIME(6),
    CONSTRAINT fk_progress_user   FOREIGN KEY (user_id)            REFERENCES users(id),
    CONSTRAINT fk_progress_module FOREIGN KEY (learning_module_id) REFERENCES learning_modules(id),
    CONSTRAINT uk_progress_user_module UNIQUE (user_id, learning_module_id)
);

-- ── achievements ──────────────────────────────────────────────
-- 1:N — badges earned by a user.
CREATE TABLE achievements (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    badge_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_ach_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── user_streaks ─────────────────────────────────────────────
-- 1:1 — each user has a single streak record.
CREATE TABLE user_streaks (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    current_streak      INT NOT NULL DEFAULT 0,
    longest_streak      INT NOT NULL DEFAULT 0,
    last_activity_date  DATE,
    CONSTRAINT fk_streak_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_streak_user UNIQUE (user_id)
);

-- ── quiz_questions ───────────────────────────────────────────
CREATE TABLE quiz_questions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    question        VARCHAR(500) NOT NULL,
    option_a        VARCHAR(200) NOT NULL,
    option_b        VARCHAR(200) NOT NULL,
    option_c        VARCHAR(200) NOT NULL,
    option_d        VARCHAR(200) NOT NULL,
    correct_answer  VARCHAR(1) NOT NULL,
    level           INT NOT NULL
);

-- ── quiz_results ─────────────────────────────────────────────
-- 1:N — each attempt at a quiz level.
CREATE TABLE quiz_results (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    level   INT NOT NULL,
    score   INT NOT NULL,
    CONSTRAINT fk_quizres_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── reward_wallets ───────────────────────────────────────────
-- 1:1 — per-user currency balances.
CREATE TABLE reward_wallets (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    coins          INT NOT NULL DEFAULT 0,
    gems           INT NOT NULL DEFAULT 0,
    premium_coins  INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_wallet_user UNIQUE (user_id)
);

-- ── reward_history ───────────────────────────────────────────
-- 1:N — ledger of coin/XP rewards earned.
CREATE TABLE reward_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    reward_type VARCHAR(40) NOT NULL,
    description VARCHAR(180) NOT NULL,
    coins       INT NOT NULL,
    xp          INT NOT NULL,
    earned_at   DATETIME(6) NOT NULL,
    CONSTRAINT fk_rewhist_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── xp_history ───────────────────────────────────────────────
-- 1:N — ledger of XP grants.
CREATE TABLE xp_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    amount      INT NOT NULL,
    reason      VARCHAR(255) NOT NULL,
    awarded_at  DATETIME(6) NOT NULL,
    CONSTRAINT fk_xphist_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── simulations ──────────────────────────────────────────────
-- 1:N — each simulation run.
CREATE TABLE simulations (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type    VARCHAR(20) NOT NULL,
    result  VARCHAR(500) NOT NULL,
    score   INT NOT NULL,
    CONSTRAINT fk_sim_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── Indexes for common lookups ───────────────────────────────
CREATE INDEX idx_quizres_level   ON quiz_results(level);
CREATE INDEX idx_progress_module ON user_progress(learning_module_id);
CREATE INDEX idx_progress_user   ON user_progress(user_id);
CREATE INDEX idx_ach_user        ON achievements(user_id);
CREATE INDEX idx_rewhist_user    ON reward_history(user_id);
CREATE INDEX idx_xphist_user     ON xp_history(user_id);
CREATE INDEX idx_sim_user        ON simulations(user_id);

