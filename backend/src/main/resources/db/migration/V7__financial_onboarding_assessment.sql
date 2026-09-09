-- =============================================================
-- FinQuest — V7: Financial Onboarding Assessment Schema
-- =============================================================

-- 1. Add assessment_completed flag to users table
ALTER TABLE users
    ADD COLUMN assessment_completed BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Create assessment_questions table
CREATE TABLE IF NOT EXISTS assessment_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_number INT NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    question_text VARCHAR(500) NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    score_a INT NOT NULL,
    score_b INT NOT NULL,
    score_c INT NOT NULL,
    score_d INT NOT NULL,
    is_risk_question BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create user_assessments history table
CREATE TABLE IF NOT EXISTS user_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    financial_score INT NOT NULL,
    literacy_level VARCHAR(20) NOT NULL,
    risk_profile VARCHAR(30) NOT NULL,
    recommendation VARCHAR(500) NOT NULL,
    answers_json TEXT NOT NULL,
    completed_at DATETIME NOT NULL,
    CONSTRAINT fk_user_assessments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_assessments_user_id ON user_assessments(user_id);

-- 4. Seed the 10 Assessment Questions (Max total score: 100)
INSERT INTO assessment_questions 
(question_number, category, question_text, option_a, option_b, option_c, option_d, score_a, score_b, score_c, score_d, is_risk_question, active)
VALUES
(
    1,
    'Income & Financial Situation',
    'How would you describe your primary income and cash flow situation?',
    'Unpredictable or irregular income with frequent cash shortfalls',
    'Steady income but expenses equal or exceed what I earn every month',
    'Steady, predictable income with a small surplus left each month',
    'Strong, diversified income streams with consistent healthy surplus',
    1, 4, 8, 10,
    FALSE, TRUE
),
(
    2,
    'Saving Habits',
    'What portion of your monthly income do you typically save or set aside?',
    'I am unable to save anything currently',
    'Less than 10% when possible',
    'Between 10% and 20% consistently every month',
    'More than 20% automated right when I get paid',
    0, 4, 8, 10,
    FALSE, TRUE
),
(
    3,
    'Budgeting',
    'How do you currently track and manage your daily and monthly expenses?',
    'I do not track expenses and check my bank balance occasionally',
    'I keep a mental estimate of major bills and expenses',
    'I use a spreadsheet or budgeting app to monitor my spending categories',
    'I follow a strict framework (like 50/30/20) and review spending monthly',
    1, 4, 8, 10,
    FALSE, TRUE
),
(
    4,
    'Debt Knowledge & Management',
    'What is your current approach to debt and borrowing?',
    'Carrying high-interest credit card/personal loans with minimum payments',
    'Paying off debt slowly without a prioritized interest-rate strategy',
    'Actively paying down high-interest debt using snowball or avalanche methods',
    'No high-interest debt, only low-interest manageable loans or completely debt-free',
    1, 4, 8, 10,
    FALSE, TRUE
),
(
    5,
    'Emergency Fund Knowledge',
    'If you faced an unexpected emergency or job loss, how long could you cover expenses?',
    'Less than 1 month or would need to borrow funds',
    'About 1 to 2 months of essential living expenses',
    '3 to 5 months of living expenses saved in a liquid account',
    '6 or more months in a dedicated high-yield savings or liquid emergency fund',
    0, 4, 8, 10,
    FALSE, TRUE
),
(
    6,
    'Investing Knowledge',
    'What is your experience and knowledge level with investments (stocks, mutual funds, ETFs)?',
    'I have never invested and do not understand how investing works',
    'I understand the basics (e.g. buying shares) but have not invested yet',
    'I invest occasionally in mutual funds, index funds, or individual stocks',
    'I have a well-diversified portfolio across asset classes with regular contributions',
    1, 4, 8, 10,
    FALSE, TRUE
),
(
    7,
    'Risk Understanding',
    'If your investment portfolio dropped 20% in one month during a market downturn, what would you do?',
    'Sell all holdings immediately to prevent any further losses',
    'Feel very anxious, stop investing, and keep money in cash',
    'Do nothing and wait for the market to recover according to my plan',
    'View it as a buying opportunity and invest more at discounted prices',
    2, 5, 8, 10,
    TRUE, TRUE
),
(
    8,
    'Financial Goals',
    'Do you have documented short-term and long-term financial goals with timelines?',
    'No clear financial goals defined yet',
    'General ideas (e.g., want to buy a house someday) without set targets',
    'Specific goals with estimated costs and target timeframes',
    'Comprehensive written financial roadmap with milestones and automated funding',
    1, 4, 8, 10,
    FALSE, TRUE
),
(
    9,
    'Financial Literacy: Compound Interest',
    'Suppose you have $100 in a savings account paying 10% compound interest per year. How much will you have after 2 years if left untouched?',
    'Less than $120',
    'Exactly $120',
    'More than $120 (specifically $121)',
    'Do not know',
    0, 3, 10, 0,
    FALSE, TRUE
),
(
    10,
    'Financial Literacy: Inflation',
    'If the interest rate on your savings account is 2% per year and inflation is 4% per year, after 1 year your purchasing power will be:',
    'More than today',
    'Exactly the same as today',
    'Less than today',
    'Do not know',
    0, 0, 10, 0,
    FALSE, TRUE
);
