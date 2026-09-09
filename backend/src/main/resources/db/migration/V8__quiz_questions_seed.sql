-- =============================================================
-- FinQuest — V8: Seed Quiz Questions for all 36 Levels
-- 5 questions per level × 36 levels = 180 questions total
-- IDEMPOTENT: Skips levels that already have active rows
-- =============================================================

-- ── BEGINNER TIER (Levels 1-12) ──────────────────────────────

-- Level 1: Money Basics
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What is the primary function of money?', 'Store of value', 'Medium of exchange', 'Unit of account', 'All of the above', 'D', 1, 'money-basics', 'EASY', 'MCQ', 'Money serves all three functions: medium of exchange, store of value, and unit of account.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 1 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which of these is NOT a form of money?', 'Cash', 'Debit card balance', 'Barter goods', 'Digital wallet balance', 'C', 1, 'money-basics', 'EASY', 'MCQ', 'Barter goods are a pre-money exchange system, not a form of money itself.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 1 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What does RBI stand for?', 'Reserve Bank of India', 'Regulated Bank of India', 'Revenue Bureau of India', 'Retail Bank of India', 'A', 1, 'money-basics', 'EASY', 'MCQ', 'The Reserve Bank of India is the central bank of India, established in 1935.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 1 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Inflation means:', 'Prices are falling', 'Purchasing power is rising', 'Prices are rising over time', 'Currency is becoming stronger', 'C', 1, 'money-basics', 'EASY', 'MCQ', 'Inflation refers to the general increase in prices over time, reducing the purchasing power of money.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 1 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A savings account primarily offers:', 'Guaranteed high returns', 'Safety and liquidity', 'Stock market exposure', 'Zero interest', 'B', 1, 'money-basics', 'EASY', 'MCQ', 'Savings accounts are designed for safety and liquidity — easy access to funds with some interest.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 1 AND active = TRUE);

-- Level 2: Budgeting & Cash Flow Foundations
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What is the 50/30/20 budgeting rule?', '50% needs, 30% wants, 20% savings', '50% savings, 30% needs, 20% wants', '50% wants, 30% savings, 20% needs', '50% tax, 30% needs, 20% wants', 'A', 2, 'budgeting', 'EASY', 'MCQ', 'The 50/30/20 rule allocates 50% to needs, 30% to wants, and 20% to savings/debt repayment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 2 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'If your income is ₹50,000 and expenses are ₹35,000, your monthly savings rate is:', '30%', '35%', '70%', '15%', 'A', 2, 'budgeting', 'EASY', 'MCQ', 'Savings rate = (₹15,000 / ₹50,000) × 100 = 30%.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 2 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which of these is a "need" in the 50/30/20 framework?', 'Dining out', 'Netflix subscription', 'Rent payment', 'New smartphone', 'C', 2, 'budgeting', 'EASY', 'MCQ', 'Needs are essential expenses required for living — rent, food, utilities, and basic transport.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 2 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Zero-based budgeting means:', 'Spending nothing each month', 'Every rupee is assigned a purpose (income − expenses = 0)', 'Saving 100% of income', 'Having no debt', 'B', 2, 'budgeting', 'EASY', 'MCQ', 'Zero-based budgeting assigns every rupee of income to a specific category so income minus expenses equals zero.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 2 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A "discretionary expense" is best described as:', 'An expense you cannot avoid', 'An optional expense based on lifestyle choices', 'A government tax', 'A fixed monthly bill', 'B', 2, 'budgeting', 'EASY', 'MCQ', 'Discretionary expenses are optional and lifestyle-driven — eating out, entertainment, travel, etc.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 2 AND active = TRUE);

-- Level 3: Savings Habits
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What is an emergency fund?', 'Money invested in stocks', 'Cash set aside for unforeseen expenses', 'A government subsidy', 'Retirement savings', 'B', 3, 'savings', 'EASY', 'MCQ', 'An emergency fund is liquid money reserved for unexpected events like job loss or medical emergencies.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 3 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The recommended size of an emergency fund is:', '1 month of expenses', '2 months of expenses', '3 to 6 months of expenses', '12 months of expenses', 'C', 3, 'savings', 'EASY', 'MCQ', 'Financial experts recommend 3 to 6 months of living expenses as an emergency fund.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 3 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT '"Pay yourself first" means:', 'Spending freely before bills', 'Saving a fixed amount immediately when income arrives', 'Paying off friends first', 'Investing all income', 'B', 3, 'savings', 'EASY', 'MCQ', 'Pay yourself first is the habit of setting aside savings the moment your income arrives, before spending.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 3 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A recurring deposit (RD) is best for:', 'One-time lump-sum investment', 'Building savings via fixed monthly deposits', 'Trading stocks', 'Borrowing money', 'B', 3, 'savings', 'EASY', 'MCQ', 'A Recurring Deposit lets you save a fixed amount every month and earn guaranteed interest.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 3 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which savings habit helps most with avoiding impulse buying?', 'Spending instantly on desires', 'Waiting 24 hours before non-essential purchases', 'Using multiple credit cards', 'Saving only when you have surplus', 'B', 3, 'savings', 'EASY', 'MCQ', 'The 24-hour rule creates time between impulse and purchase, reducing unnecessary spending.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 3 AND active = TRUE);

-- Level 4: Banking & Credit
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What does EMI stand for?', 'Equal Monthly Income', 'Equated Monthly Instalment', 'Extra Money Interest', 'Estimated Market Investment', 'B', 4, 'banking', 'EASY', 'MCQ', 'EMI stands for Equated Monthly Instalment — the fixed payment made to repay a loan over time.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 4 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A credit score in India is most commonly measured by:', 'SEBI', 'CIBIL / TransUnion', 'RBI', 'NSE', 'B', 4, 'banking', 'EASY', 'MCQ', 'CIBIL (TransUnion) provides the most widely used credit score in India, ranging from 300 to 900.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 4 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A CIBIL score of 750 or above is considered:', 'Poor', 'Fair', 'Good to excellent', 'Not applicable', 'C', 4, 'banking', 'EASY', 'MCQ', 'A CIBIL score of 750+ is generally considered good and improves loan approval chances.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 4 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What is the benefit of autopaying your credit card bill in full every month?', 'Earns more reward points', 'Avoids interest charges and builds credit history', 'Increases credit limit automatically', 'Has no benefit', 'B', 4, 'banking', 'EASY', 'MCQ', 'Paying credit card bills in full eliminates interest charges and builds a positive repayment history.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 4 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The credit utilisation ratio should ideally be kept below:', '90%', '75%', '50%', '30%', 'D', 4, 'banking', 'EASY', 'MCQ', 'Credit utilisation below 30% signals responsible credit usage and helps maintain a good CIBIL score.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 4 AND active = TRUE);

-- Level 5: Understanding Interest
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Compound interest is calculated on:', 'Principal only', 'Interest only', 'Principal + previously accumulated interest', 'Future income', 'C', 5, 'interest', 'EASY', 'MCQ', 'Compound interest is calculated on the principal plus any interest already earned — interest on interest.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 5 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Rule of 72 estimates how long it takes to double your money. If the rate is 8%, it doubles in approximately:', '6 years', '9 years', '12 years', '18 years', 'B', 5, 'interest', 'EASY', 'MCQ', '72 ÷ 8 = 9 years. The Rule of 72 divides 72 by the annual return rate.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 5 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which frequency of compounding gives the highest return for the same annual rate?', 'Annual', 'Semi-annual', 'Monthly', 'Daily', 'D', 5, 'interest', 'EASY', 'MCQ', 'More frequent compounding means interest is added more often, resulting in faster growth.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 5 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Simple interest on ₹10,000 at 5% per year for 3 years is:', '₹500', '₹1,000', '₹1,500', '₹1,576', 'C', 5, 'interest', 'EASY', 'MCQ', 'Simple interest = P × R × T = 10,000 × 0.05 × 3 = ₹1,500.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 5 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which is true about compound interest compared to simple interest over long periods?', 'They are always equal', 'Simple interest grows faster', 'Compound interest grows significantly faster', 'Compound interest only helps for large amounts', 'C', 5, 'interest', 'EASY', 'MCQ', 'Compound interest accelerates growth exponentially over time; simple interest grows linearly.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 5 AND active = TRUE);

-- Level 6: Debt Management
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Debt Avalanche method prioritises paying off:', 'Smallest balance first', 'Newest debt first', 'Highest-interest-rate debt first', 'Oldest debt first', 'C', 6, 'debt', 'EASY', 'MCQ', 'Debt Avalanche targets highest interest debts first, minimising total interest paid.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 6 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Debt Snowball method prioritises paying off:', 'Highest interest rate first', 'Smallest balance first', 'Largest balance first', 'Most recent loan first', 'B', 6, 'debt', 'EASY', 'MCQ', 'Debt Snowball clears smallest debts first for psychological momentum, then moves to larger debts.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 6 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Debt consolidation means:', 'Taking more loans', 'Combining multiple debts into a single lower-interest loan', 'Paying off all debt at once', 'Declaring bankruptcy', 'B', 6, 'debt', 'EASY', 'MCQ', 'Debt consolidation merges multiple debts into one, ideally with a lower interest rate and simplified repayment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 6 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A personal loan at 16% interest vs. a credit card at 36% — which is "worse" to carry as long-term debt?', 'Personal loan', 'Credit card', 'Both are equal', 'Neither — all debt is fine', 'B', 6, 'debt', 'EASY', 'MCQ', 'Credit card debt at 36% is far more expensive long-term and should be cleared first.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 6 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What is the debt-to-income (DTI) ratio?', 'Monthly debt payments ÷ monthly gross income × 100', 'Total debt ÷ total savings', 'Income ÷ total assets', 'Monthly expenses ÷ monthly income', 'A', 6, 'debt', 'EASY', 'MCQ', 'DTI = (total monthly debt payments / gross monthly income) × 100. Lenders prefer DTI below 36%.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 6 AND active = TRUE);

-- Level 7: Insurance Basics
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What is the primary purpose of term life insurance?', 'Build savings over time', 'Provide a death benefit to nominees during the policy term', 'Earn investment returns', 'Cover hospitalisation expenses', 'B', 7, 'insurance', 'EASY', 'MCQ', 'Term life insurance provides a lump-sum payout to nominees if the insured dies during the policy term.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 7 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A health insurance deductible is:', 'The premium you pay monthly', 'The amount you pay out-of-pocket before insurance covers costs', 'The maximum coverage limit', 'A tax deduction on premiums', 'B', 7, 'insurance', 'EASY', 'MCQ', 'A deductible is the out-of-pocket amount you pay before the insurer begins covering expenses.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 7 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'ULIP combines:', 'Savings and loans', 'Life insurance and investment', 'Health insurance and term insurance', 'Mutual funds and fixed deposits', 'B', 7, 'insurance', 'EASY', 'MCQ', 'A Unit Linked Insurance Plan (ULIP) combines life insurance cover with market-linked investment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 7 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Under Section 80C, premiums paid for life insurance are deductible up to:', '₹50,000', '₹1,00,000', '₹1,50,000', '₹2,00,000', 'C', 7, 'insurance', 'EASY', 'MCQ', 'Section 80C allows deductions of up to ₹1,50,000 per year including life insurance premiums.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 7 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which type of insurance protects against loss of income due to disability?', 'Term life insurance', 'Health insurance', 'Disability income insurance', 'Vehicle insurance', 'C', 7, 'insurance', 'EASY', 'MCQ', 'Disability income insurance provides income replacement if you become unable to work due to illness or injury.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 7 AND active = TRUE);

-- Level 8: Tax Basics
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'TDS stands for:', 'Tax Deduction Scheme', 'Tax Deducted at Source', 'Total Deductible Sum', 'Tax Deferral System', 'B', 8, 'taxation', 'EASY', 'MCQ', 'TDS (Tax Deducted at Source) is income tax deducted by the payer at the time of payment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 8 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Under the new tax regime (2024-25), income up to ₹3 lakh is:', 'Taxed at 5%', 'Taxed at 10%', 'Exempt from tax', 'Taxed at 30%', 'C', 8, 'taxation', 'EASY', 'MCQ', 'Under the new tax regime, income up to ₹3 lakh is exempt from income tax.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 8 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'What does Section 80C allow you to do?', 'Avoid paying GST', 'Deduct up to ₹1.5 lakh from taxable income through specified investments', 'Claim unlimited deductions', 'Reduce your GST rate', 'B', 8, 'taxation', 'EASY', 'MCQ', 'Section 80C allows deductions of up to ₹1,50,000 from taxable income through investments like PPF, ELSS, and LIC premiums.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 8 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Long-term capital gains (LTCG) on equity mutual funds exceeding ₹1 lakh are taxed at:', '0%', '10%', '15%', '20%', 'B', 8, 'taxation', 'EASY', 'MCQ', 'LTCG on equity (held > 1 year) above ₹1 lakh is taxed at 10% without indexation benefit.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 8 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Form 26AS is:', 'A loan application form', 'A tax credit statement showing TDS and advance tax', 'A GST return form', 'An investment declaration form', 'B', 8, 'taxation', 'EASY', 'MCQ', 'Form 26AS is a tax statement that shows TDS deducted, advance tax paid, and other tax credits for a financial year.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 8 AND active = TRUE);

-- Level 9: Good Debt vs Bad Debt
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which is typically considered "good debt"?', 'Credit card debt for a vacation', 'Personal loan for a phone', 'Education loan to build career skills', 'Payday loan', 'C', 9, 'debt-management', 'EASY', 'MCQ', 'Good debt is borrowed money that increases income or net worth — like education loans or home loans.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 9 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A payday loan is considered bad debt mainly because:', 'It requires collateral', 'It has extremely high interest rates and short repayment periods', 'It is only for businesses', 'It has no interest', 'B', 9, 'debt-management', 'EASY', 'MCQ', 'Payday loans typically carry very high APR (often 300%+) and can trap borrowers in debt cycles.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 9 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A home loan EMI can be considered "productive debt" because:', 'Banks offer it at low rates', 'The asset generally appreciates over time', 'You can sell the house anytime', 'Tax benefit makes it free', 'B', 9, 'debt-management', 'EASY', 'MCQ', 'A home loan builds equity in an appreciating asset, making it an example of productive/good debt.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 9 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'If your DTI exceeds 50%, lenders typically:', 'Offer better interest rates', 'Approve loans easily', 'View you as high-risk and may reject applications', 'Have no concern', 'C', 9, 'debt-management', 'EASY', 'MCQ', 'A DTI above 50% signals that more than half your income goes to debt, making lenders cautious.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 9 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Prepaying a high-interest loan helps because:', 'It lowers your credit score', 'It reduces total interest paid over the loan term', 'Banks always charge a prepayment penalty', 'It has no financial benefit', 'B', 9, 'debt-management', 'EASY', 'MCQ', 'Prepaying reduces the outstanding principal, which lowers the interest calculated going forward.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 9 AND active = TRUE);

-- Level 10: Financial Goals
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A SMART financial goal is:', 'Simple, Meaningful, Achievable, Reliable, Timely', 'Specific, Measurable, Achievable, Relevant, Time-bound', 'Savings, Monthly, Annual, Returns, Targets', 'Short, Medium, Achievable, Realistic, Timely', 'B', 10, 'financial-goals', 'EASY', 'MCQ', 'SMART stands for Specific, Measurable, Achievable, Relevant, and Time-bound.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 10 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A short-term financial goal typically has a time horizon of:', '10 to 30 years', '5 to 10 years', 'Less than 1 to 3 years', 'More than 30 years', 'C', 10, 'financial-goals', 'EASY', 'MCQ', 'Short-term goals are typically achievable within 1-3 years (e.g., emergency fund, vacation fund).', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 10 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which goal is an example of a long-term financial goal?', 'Paying next month\'s rent', 'Buying a laptop in 6 months', 'Building retirement corpus over 25 years', 'Repaying a 3-month personal loan', 'C', 10, 'financial-goals', 'EASY', 'MCQ', 'Long-term goals like retirement planning require 10+ years of disciplined saving and investing.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 10 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Why is it important to write down financial goals?', 'It is legally required', 'It increases accountability and follow-through', 'Banks require it for loans', 'It automatically grows your money', 'B', 10, 'financial-goals', 'EASY', 'MCQ', 'Written goals improve commitment, make progress measurable, and increase the likelihood of achievement.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 10 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Goal-based investing means:', 'Investing only in gold', 'Linking each investment to a specific financial goal with a timeline', 'Investing for maximum short-term returns', 'Investing without any plan', 'B', 10, 'financial-goals', 'EASY', 'MCQ', 'Goal-based investing aligns investments to specific goals with matching time horizons and risk levels.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 10 AND active = TRUE);

-- Level 11: Personal Cash Flow
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Net cash flow is calculated as:', 'Assets minus liabilities', 'Total income minus total expenses', 'Savings minus investments', 'Revenue minus taxes', 'B', 11, 'cash-flow', 'EASY', 'MCQ', 'Net cash flow = Total income − Total expenses. Positive means surplus; negative means deficit.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 11 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A "cash flow statement" tracks:', 'Your net worth', 'Stock portfolio performance', 'The actual movement of money in and out of your accounts', 'Tax liability', 'C', 11, 'cash-flow', 'EASY', 'MCQ', 'A personal cash flow statement records all income sources and all expenses over a period.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 11 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Fixed expenses are best described as:', 'Expenses that change monthly', 'Expenses that remain constant each month (rent, EMI)', 'One-time large purchases', 'Taxes and fees', 'B', 11, 'cash-flow', 'EASY', 'MCQ', 'Fixed expenses are predictable and constant each month, making them easy to plan for in a budget.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 11 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'To improve monthly cash flow, the best approach is:', 'Increase income, reduce unnecessary expenses, or both', 'Take more loans', 'Invest all income', 'Ignore small expenses', 'A', 11, 'cash-flow', 'EASY', 'MCQ', 'Improving cash flow requires either earning more, spending less on non-essentials, or a combination of both.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 11 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Lifestyle inflation refers to:', 'Rising cost of living due to government policy', 'Increasing spending as income rises without increasing savings', 'Medical cost inflation', 'Investment portfolio growth', 'B', 11, 'cash-flow', 'EASY', 'MCQ', 'Lifestyle inflation (lifestyle creep) occurs when spending rises proportionally with income, preventing wealth accumulation.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 11 AND active = TRUE);

-- Level 12: Beginner Finance Challenge
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which of the following is NOT a liquid asset?', 'Savings account balance', 'Cash in hand', 'Real estate property', 'Short-term government bonds', 'C', 12, 'mixed-beginner', 'MEDIUM', 'MCQ', 'Real estate is illiquid — it cannot be quickly converted to cash without significant time and cost.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 12 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'If inflation is 6% and your FD earns 5%, your real return is:', '+11%', '+1%', '-1%', '0%', 'C', 12, 'mixed-beginner', 'MEDIUM', 'MCQ', 'Real return = Nominal return − Inflation = 5% − 6% = −1%. Your purchasing power is decreasing.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 12 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Net worth is calculated as:', 'Income minus expenses', 'Total assets minus total liabilities', 'Savings plus investments', 'Annual income times 10', 'B', 12, 'mixed-beginner', 'MEDIUM', 'MCQ', 'Net worth = Total Assets − Total Liabilities. It is the most accurate measure of financial health.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 12 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which comes FIRST in building financial health?', 'Investing in equity', 'Taking a home loan', 'Building an emergency fund', 'Buying insurance', 'C', 12, 'mixed-beginner', 'MEDIUM', 'MCQ', 'An emergency fund provides the financial cushion needed before investing or taking on financial obligations.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 12 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The power of starting to invest early is best explained by:', 'Higher risk tolerance when young', 'More time for compound interest to work', 'Lower tax rates for young investors', 'Government subsidies for young savers', 'B', 12, 'mixed-beginner', 'MEDIUM', 'MCQ', 'Starting early gives compound interest more time to grow wealth exponentially — time is the most important factor.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 12 AND active = TRUE);

-- ── INTERMEDIATE TIER (Levels 13-24) ─────────────────────────

-- Level 13: Investing Basics
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Diversification in investing means:', 'Putting all money in one asset', 'Spreading investments across different assets to reduce risk', 'Investing only in safe instruments', 'Buying and selling frequently', 'B', 13, 'investing-basics', 'MEDIUM', 'MCQ', 'Diversification reduces risk by spreading investments, so a loss in one asset is offset by gains in others.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 13 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The risk-return tradeoff states that:', 'Higher risk always means guaranteed higher returns', 'Lower risk investments typically offer lower expected returns', 'Safe investments have higher returns', 'Risk and return are unrelated', 'B', 13, 'investing-basics', 'MEDIUM', 'MCQ', 'Higher potential returns generally come with higher risk; lower-risk instruments offer lower expected returns.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 13 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'An equity share represents:', 'A loan given to a company', 'Ownership stake in a company', 'A guaranteed return instrument', 'A government bond', 'B', 13, 'investing-basics', 'MEDIUM', 'MCQ', 'An equity share (stock) represents partial ownership in a company, entitling the holder to dividends and voting rights.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 13 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A mutual fund is best described as:', 'A type of bank account', 'A pooled investment vehicle managed by professionals', 'A government savings scheme', 'Direct stock buying', 'B', 13, 'investing-basics', 'MEDIUM', 'MCQ', 'A mutual fund pools money from many investors to invest in a diversified portfolio managed by fund managers.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 13 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'NAV in mutual funds stands for:', 'Net Annual Value', 'Net Asset Value', 'National Asset Value', 'Net Allocation Volume', 'B', 13, 'investing-basics', 'MEDIUM', 'MCQ', 'NAV (Net Asset Value) is the per-unit price of a mutual fund, calculated as (total assets − liabilities) ÷ outstanding units.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 13 AND active = TRUE);

-- Level 14: Stock Market Fundamentals
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Sensex tracks the top companies listed on:', 'NSE', 'BSE', 'MCX', 'SEBI', 'B', 14, 'stock-market', 'MEDIUM', 'MCQ', 'The Sensex (BSE Sensex) is the benchmark index of the Bombay Stock Exchange, tracking 30 large companies.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 14 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'NIFTY 50 represents:', 'The 50 most expensive stocks in India', 'The top 50 companies by market capitalisation on NSE', 'NSE listing fee index', '50 government bonds', 'B', 14, 'stock-market', 'MEDIUM', 'MCQ', 'Nifty 50 is the benchmark index of NSE, comprising 50 large-cap companies across various sectors.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 14 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A bull market is characterised by:', 'Falling stock prices', 'Rising stock prices and investor optimism', 'High volatility with no trend', 'A market closed for trading', 'B', 14, 'stock-market', 'MEDIUM', 'MCQ', 'A bull market is defined by sustained rising prices (typically 20%+ gain) and positive investor sentiment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 14 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Market capitalisation is calculated as:', 'Share price × earnings per share', 'Share price × total shares outstanding', 'Net profit × dividend yield', 'Revenue × P/E ratio', 'B', 14, 'stock-market', 'MEDIUM', 'MCQ', 'Market cap = Share price × total outstanding shares. It measures the total market value of a company.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 14 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A Demat account is used for:', 'Taking loans from banks', 'Holding securities in electronic form', 'Filing income tax returns', 'Paying insurance premiums', 'B', 14, 'stock-market', 'MEDIUM', 'MCQ', 'A Demat (Dematerialised) account holds your shares and securities in digital form, eliminating physical certificates.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 14 AND active = TRUE);

-- Level 15: Mutual Funds Deep Dive
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'An ELSS fund qualifies for tax deduction under:', 'Section 80D', 'Section 80C', 'Section 10(10D)', 'Section 80G', 'B', 15, 'mutual-funds', 'MEDIUM', 'MCQ', 'ELSS (Equity Linked Savings Scheme) qualifies for up to ₹1.5 lakh deduction under Section 80C with a 3-year lock-in.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 15 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Expense ratio in a mutual fund is:', 'The fund\'s profit margin', 'Annual fee charged by the fund for management, expressed as % of AUM', 'Tax paid on gains', 'Entry load on investment', 'B', 15, 'mutual-funds', 'MEDIUM', 'MCQ', 'The expense ratio is the annual cost of managing a mutual fund, deducted from the NAV as a percentage of assets.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 15 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A debt mutual fund primarily invests in:', 'Company stocks', 'Bonds, government securities, and fixed-income instruments', 'Real estate', 'Commodities like gold', 'B', 15, 'mutual-funds', 'MEDIUM', 'MCQ', 'Debt funds invest in fixed-income securities like bonds and T-bills, offering lower risk than equity funds.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 15 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'SIP (Systematic Investment Plan) helps through:', 'Guaranteed returns', 'Rupee cost averaging and disciplined investing', 'Avoiding market risk completely', 'One-time large investment', 'B', 15, 'mutual-funds', 'MEDIUM', 'MCQ', 'SIP invests a fixed amount regularly; rupee cost averaging means you buy more units when prices are low.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 15 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'An index fund aims to:', 'Beat the market consistently', 'Replicate the performance of a market index like Nifty 50', 'Invest only in government bonds', 'Actively pick best-performing stocks', 'B', 15, 'mutual-funds', 'MEDIUM', 'MCQ', 'Index funds passively replicate a benchmark index, offering low-cost broad market exposure.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 15 AND active = TRUE);

-- Level 16: SIP & Compound Interest
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Investing ₹5,000/month via SIP at 12% annual return for 20 years gives approximately:', '₹12 lakh', '₹50 lakh', '₹1.2 crore', '₹3 crore', 'B', 16, 'sip-compound', 'MEDIUM', 'MCQ', 'SIP of ₹5,000/month at 12% for 20 years yields approximately ₹50 lakh — a massive wealth creation over time.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 16 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Step-up SIP means:', 'Stopping SIP when markets fall', 'Increasing SIP amount annually in line with income growth', 'Investing a lump sum once a year', 'Switching between funds', 'B', 16, 'sip-compound', 'MEDIUM', 'MCQ', 'Step-up SIP (top-up SIP) allows you to increase the SIP amount periodically, accelerating wealth creation.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 16 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The biggest advantage of starting a SIP at age 25 vs. 35 is:', 'Lower brokerage', 'Tax exemptions', '10 extra years of compounding', 'Higher guaranteed returns', 'C', 16, 'sip-compound', 'MEDIUM', 'MCQ', 'Starting 10 years earlier allows compounding to work longer, potentially doubling or tripling the final corpus.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 16 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'CAGR (Compound Annual Growth Rate) measures:', 'Monthly average returns', 'Total absolute return', 'Annualised growth rate assuming compounding', 'Dividend yield', 'C', 16, 'sip-compound', 'MEDIUM', 'MCQ', 'CAGR represents the mean annual growth rate of an investment over a specified period, assuming compound growth.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 16 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Rupee cost averaging benefits the investor by:', 'Guaranteeing profit', 'Reducing average cost per unit by buying more units when prices are low', 'Avoiding market entry entirely', 'Maximising returns in bull markets only', 'B', 16, 'sip-compound', 'MEDIUM', 'MCQ', 'With SIP, the same amount buys more units when prices are low, reducing the average purchase cost over time.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 16 AND active = TRUE);

-- Level 17: Bonds & Fixed Income
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'When interest rates rise, bond prices generally:', 'Rise', 'Fall', 'Stay the same', 'Double', 'B', 17, 'bonds', 'MEDIUM', 'MCQ', 'Bond prices and interest rates move inversely — when rates rise, existing bonds with lower rates become less valuable.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 17 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A government bond is generally considered:', 'Very high risk', 'Moderate risk', 'Very low risk', 'No information available', 'C', 17, 'bonds', 'MEDIUM', 'MCQ', 'Government bonds (G-secs) are backed by sovereign guarantee, making them among the safest investments.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 17 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Yield To Maturity (YTM) represents:', 'The annual coupon payment only', 'The total return if the bond is held until maturity', 'The current market price of the bond', 'The credit rating of the issuer', 'B', 17, 'bonds', 'MEDIUM', 'MCQ', 'YTM is the total return anticipated if the bond is held to maturity, including coupon payments and capital gains/losses.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 17 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A corporate bond rated "AAA" is:', 'The riskiest type', 'Highest quality with lowest default risk', 'Unrated and speculative', 'Only for retail investors', 'B', 17, 'bonds', 'MEDIUM', 'MCQ', 'AAA is the highest credit rating, indicating the issuer has an extremely strong capacity to meet financial commitments.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 17 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Duration in bond investing measures:', 'The maturity date only', 'Sensitivity of bond price to interest rate changes', 'The annual coupon rate', 'The credit rating', 'B', 17, 'bonds', 'MEDIUM', 'MCQ', 'Duration measures how much a bond\'s price will change with a 1% change in interest rates — higher duration = more sensitivity.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 17 AND active = TRUE);

-- Level 18: Asset Allocation
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Asset allocation is about:', 'Picking the best single stock', 'Distributing investments across asset classes based on risk tolerance and goals', 'Timing the market perfectly', 'Keeping all money in a bank', 'B', 18, 'asset-allocation', 'MEDIUM', 'MCQ', 'Asset allocation distributes investments across stocks, bonds, gold, real estate etc., balancing risk and reward.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 18 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The "100 minus age" rule suggests that a 30-year-old should have what percentage in equities?', '30%', '70%', '50%', '100%', 'B', 18, 'asset-allocation', 'MEDIUM', 'MCQ', '100 − 30 = 70% in equities. The rule increases debt allocation as you age, reducing risk exposure.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 18 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Rebalancing a portfolio means:', 'Selling all holdings', 'Restoring the original target allocation by buying/selling assets', 'Switching to only government bonds', 'Adding more equity every year', 'B', 18, 'asset-allocation', 'MEDIUM', 'MCQ', 'Rebalancing restores the desired asset mix when market movements cause it to drift from the target allocation.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 18 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'For a 25-year retirement goal, which allocation is typically appropriate?', 'All fixed deposits', '90% bonds, 10% equity', '70% equity, 30% debt', '100% gold', 'C', 18, 'asset-allocation', 'MEDIUM', 'MCQ', 'A long investment horizon allows higher equity exposure for growth, with debt providing stability.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 18 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Correlation in portfolio management refers to:', 'The return on a single stock', 'How two assets move relative to each other', 'The age of your investments', 'The management fee of a fund', 'B', 18, 'asset-allocation', 'MEDIUM', 'MCQ', 'Low or negative correlation between assets improves diversification — if one falls, the other may rise.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 18 AND active = TRUE);

-- Level 19: Retirement Planning
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The National Pension System (NPS) in India offers:', 'Only insurance coverage', 'Market-linked retirement savings with partial tax benefits', 'Fixed returns like PPF', 'No tax benefits', 'B', 19, 'retirement', 'MEDIUM', 'MCQ', 'NPS is a market-linked retirement scheme with Tier 1 accounts offering tax benefits under Sections 80C and 80CCD(1B).', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 19 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The 4% rule for retirement withdrawal states that:', 'Withdraw 4% of savings monthly', 'Withdraw 4% of portfolio annually to sustain a 25-30 year retirement', 'Invest 4% of income in NPS', 'Pay 4% tax on withdrawals', 'B', 19, 'retirement', 'MEDIUM', 'MCQ', 'The 4% rule suggests you can withdraw 4% of your portfolio annually and sustain it for 25-30 years.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 19 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'To retire comfortably, most financial planners suggest having a corpus of at least:', '5 times annual expenses', '10-15 times annual expenses', '25 times annual expenses', '50 times annual expenses', 'C', 19, 'retirement', 'MEDIUM', 'MCQ', 'A corpus of 25× annual expenses supports the 4% withdrawal rule for a sustainable 25-30 year retirement.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 19 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'EPF (Employee Provident Fund) contributions earn:', 'Market-linked returns like equity', 'Government-guaranteed interest', 'Zero returns', 'Returns linked to gold price', 'B', 19, 'retirement', 'MEDIUM', 'MCQ', 'EPF earns a government-declared interest rate (around 8%) and offers guaranteed, tax-efficient growth.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 19 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Inflation risk in retirement means:', 'Running out of money due to rising prices eroding purchasing power', 'Market crashes during retirement', 'Tax increases on pension income', 'Healthcare costs falling', 'A', 19, 'retirement', 'MEDIUM', 'MCQ', 'If inflation grows faster than your withdrawals can sustain, your purchasing power declines over retirement years.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 19 AND active = TRUE);

-- Level 20: Real Estate Investing
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A REIT (Real Estate Investment Trust) allows investors to:', 'Own physical property directly', 'Invest in income-generating real estate without buying property', 'Get government housing subsidies', 'Avoid capital gains tax on real estate', 'B', 20, 'real-estate', 'MEDIUM', 'MCQ', 'REITs are listed entities that own income-producing real estate, letting retail investors participate with small amounts.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 20 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The cap rate (capitalisation rate) in real estate is:', 'Mortgage interest rate', 'Net operating income ÷ property value × 100', 'Down payment percentage', 'Stamp duty rate', 'B', 20, 'real-estate', 'MEDIUM', 'MCQ', 'Cap rate = (NOI / Property Value) × 100. It measures the expected return on a real estate investment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 20 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Real estate investment is considered illiquid because:', 'Property prices never rise', 'It cannot be quickly converted to cash without loss or delay', 'Banks do not lend on property', 'It has no resale value', 'B', 20, 'real-estate', 'MEDIUM', 'MCQ', 'Real estate takes time to sell (weeks to months), making it less liquid compared to stocks or mutual funds.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 20 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The main advantage of REITs over direct property investment is:', 'Higher rental income', 'Liquidity, diversification, and lower entry cost', 'Tax-free income', 'No regulation by SEBI', 'B', 20, 'real-estate', 'MEDIUM', 'MCQ', 'REITs are exchange-listed, can be bought/sold like stocks, and allow diversified real estate exposure with small investments.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 20 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Stamp duty on property purchase in India is paid by:', 'The seller', 'The buyer', 'Both equally', 'The bank', 'B', 20, 'real-estate', 'MEDIUM', 'MCQ', 'Stamp duty is paid by the buyer and varies by state (typically 3-7% of property value). It legalises the transfer.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 20 AND active = TRUE);

-- Level 21: Portfolio Management
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Beta measures:', 'A portfolio\'s return', 'A stock\'s volatility relative to the overall market', 'The P/E ratio of a stock', 'Dividend yield', 'B', 21, 'portfolio-mgmt', 'MEDIUM', 'MCQ', 'Beta measures a stock\'s price volatility relative to the market. Beta > 1 means more volatile than market.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 21 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Sharpe ratio measures:', 'Absolute return of a portfolio', 'Return per unit of risk (total risk)', 'Market volatility index', 'P/E ratio of a portfolio', 'B', 21, 'portfolio-mgmt', 'MEDIUM', 'MCQ', 'Sharpe ratio = (Portfolio return − Risk-free rate) / Standard deviation. Higher is better.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 21 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Systematic risk (market risk) in investing:', 'Can be eliminated by diversification', 'Cannot be diversified away', 'Applies only to bonds', 'Only affects small-cap stocks', 'B', 21, 'portfolio-mgmt', 'MEDIUM', 'MCQ', 'Systematic risk affects the entire market (wars, recessions, rate changes) and cannot be diversified away.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 21 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Dollar-cost averaging (or rupee-cost averaging) reduces:', 'Total returns', 'The impact of market timing risk', 'Inflation', 'Tax liability', 'B', 21, 'portfolio-mgmt', 'MEDIUM', 'MCQ', 'Regular fixed investments reduce timing risk — you automatically buy more units when prices are low.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 21 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'An equity portfolio\'s alpha measures:', 'Market return', 'Excess return generated over a benchmark', 'Total risk in the portfolio', 'Beta relative to Nifty', 'B', 21, 'portfolio-mgmt', 'MEDIUM', 'MCQ', 'Alpha is the excess return of an investment relative to a benchmark index — positive alpha means outperformance.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 21 AND active = TRUE);

-- Level 22: Tax-Efficient Investing
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Tax-loss harvesting involves:', 'Avoiding all taxable investments', 'Selling loss-making investments to offset capital gains tax', 'Buying only tax-free bonds', 'Deferring taxes indefinitely', 'B', 22, 'tax-investing', 'MEDIUM', 'MCQ', 'Tax-loss harvesting offsets capital gains with realised losses, reducing overall tax liability.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 22 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'PPF (Public Provident Fund) falls under which tax category?', 'Taxable-Taxable-Taxable (TTT)', 'Exempt-Taxable-Exempt (ETE)', 'Exempt-Exempt-Exempt (EEE)', 'Taxable-Exempt-Exempt (TEE)', 'C', 22, 'tax-investing', 'MEDIUM', 'MCQ', 'PPF enjoys EEE status — investments, interest earned, and maturity amount are all exempt from tax.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 22 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Short-term capital gains (STCG) on equity held less than 1 year are taxed at:', '0%', '10%', '15%', '20%', 'C', 22, 'tax-investing', 'MEDIUM', 'MCQ', 'STCG on equity investments held for less than 12 months is taxed at 15% flat under Indian tax law.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 22 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Additional NPS contribution under Section 80CCD(1B) allows deduction of up to:', '₹50,000', '₹1,00,000', '₹1,50,000', '₹2,00,000', 'A', 22, 'tax-investing', 'MEDIUM', 'MCQ', 'Section 80CCD(1B) allows an additional ₹50,000 deduction for NPS contributions, over and above the 80C limit.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 22 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'ELSS lock-in period is:', '1 year', '3 years', '5 years', '7 years', 'B', 22, 'tax-investing', 'MEDIUM', 'MCQ', 'ELSS funds have a mandatory 3-year lock-in period, the shortest among all 80C options.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 22 AND active = TRUE);

-- Level 23: Behavioural Finance
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Loss aversion in behavioural finance means:', 'Investors prefer risky assets', 'The pain of losing money is felt more strongly than the pleasure of gaining the same amount', 'Investors are rational about risk', 'Losses are taxed more heavily', 'B', 23, 'behavioural-finance', 'MEDIUM', 'MCQ', 'Research shows losses feel about 2× more painful than equivalent gains feel pleasurable — driving irrational decisions.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 23 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Herd mentality in investing leads to:', 'Independent rational decisions', 'Following crowd behaviour, buying when markets rise and panic-selling when they fall', 'Better diversification', 'Lower volatility', 'B', 23, 'behavioural-finance', 'MEDIUM', 'MCQ', 'Herd behaviour causes investors to follow the crowd, amplifying market bubbles and crashes.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 23 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Confirmation bias in investing is:', 'Seeking information that challenges your beliefs', 'Favouring information that confirms existing investment views', 'Diversifying based on facts', 'A proven strategy for returns', 'B', 23, 'behavioural-finance', 'MEDIUM', 'MCQ', 'Confirmation bias leads investors to seek only information that confirms their existing views, ignoring contrary evidence.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 23 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The disposition effect in investing refers to:', 'Holding winners too long and selling losers too quickly', 'Selling winners too early and holding losers too long', 'Buying only growth stocks', 'Diversifying excessively', 'B', 23, 'behavioural-finance', 'MEDIUM', 'MCQ', 'The disposition effect causes investors to sell winning investments too early and hold losing ones too long, hoping for recovery.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 23 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'To counter behavioural biases, the best strategy is:', 'Trading more frequently', 'Following a pre-defined investment plan and automating investments', 'Watching financial news daily', 'Investing based on tips', 'B', 23, 'behavioural-finance', 'MEDIUM', 'MCQ', 'A rules-based investment plan and automation (like SIPs) remove emotion from decision-making, countering biases.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 23 AND active = TRUE);

-- Level 24: Intermediate Finance Challenge
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A company with P/E ratio of 10 vs. sector average of 25 may indicate:', 'Overvaluation', 'Undervaluation relative to peers', 'High growth expectations', 'Poor dividend history', 'B', 24, 'mixed-intermediate', 'MEDIUM', 'MCQ', 'A P/E significantly below the sector average may indicate undervaluation — but requires further fundamental analysis.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 24 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The efficient market hypothesis (EMH) suggests:', 'Markets are always wrong', 'Stock prices reflect all available information at any point', 'Technical analysis always works', 'Insider trading is profitable long-term', 'B', 24, 'mixed-intermediate', 'MEDIUM', 'MCQ', 'EMH states that stock prices always incorporate all public information, making it impossible to consistently beat the market.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 24 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Sovereign Gold Bonds (SGBs) offer:', 'Physical gold storage', 'Gold price appreciation + 2.5% annual interest, fully tax-free at maturity', 'No returns', 'Market-linked equity returns', 'B', 24, 'mixed-intermediate', 'MEDIUM', 'MCQ', 'SGBs offer gold price exposure plus 2.5% p.a. interest; capital gains at maturity are exempt for individuals.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 24 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Which instrument combines equity and debt in one structure?', 'FD', 'Balanced Advantage Fund (Dynamic Asset Allocation)', 'PPF', 'NPS Tier 2', 'B', 24, 'mixed-intermediate', 'MEDIUM', 'MCQ', 'Balanced Advantage Funds dynamically allocate between equity and debt based on market conditions.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 24 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Portfolio turnover ratio indicates:', 'The fund manager\'s salary', 'How frequently the fund buys and sells securities in a year', 'The annual expense ratio', 'The fund\'s NAV growth', 'B', 24, 'mixed-intermediate', 'MEDIUM', 'MCQ', 'High portfolio turnover means frequent trading, which increases transaction costs and can reduce net returns.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 24 AND active = TRUE);

-- ── ADVANCED TIER (Levels 25-36) ─────────────────────────────

-- Level 25: Advanced Investing
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Free Cash Flow (FCF) is calculated as:', 'Revenue minus taxes', 'Operating cash flow minus capital expenditures', 'Net profit plus depreciation', 'EBITDA minus interest', 'B', 25, 'advanced-investing', 'HARD', 'MCQ', 'FCF = Operating Cash Flow − Capital Expenditures. It represents cash generated after maintaining/expanding asset base.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 25 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'EV/EBITDA is preferred over P/E for valuing companies because:', 'It is simpler to calculate', 'It is capital-structure neutral and removes distortions from debt and taxes', 'It always gives a lower number', 'It works only for startups', 'B', 25, 'advanced-investing', 'HARD', 'MCQ', 'EV/EBITDA compares total enterprise value to earnings before financing and accounting effects, enabling cleaner cross-company comparison.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 25 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Discounted Cash Flow (DCF) valuation relies on:', 'Historical stock prices', 'Projecting future cash flows and discounting to present value', 'Comparing P/E to competitors', 'Current book value', 'B', 25, 'advanced-investing', 'HARD', 'MCQ', 'DCF values a company by discounting projected future cash flows at the weighted average cost of capital (WACC).', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 25 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Return on Equity (ROE) measures:', 'How efficiently assets generate revenue', 'Profit generated relative to shareholders\' equity', 'Total debt relative to equity', 'Cash flow from operations', 'B', 25, 'advanced-investing', 'HARD', 'MCQ', 'ROE = Net Income / Shareholders\' Equity. It measures how effectively management uses equity to generate profit.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 25 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The margin of safety in value investing refers to:', 'Buying at a price well below intrinsic value to reduce downside risk', 'Setting a stop loss on all trades', 'Diversifying into 100 stocks', 'Keeping 50% in cash always', 'A', 25, 'advanced-investing', 'HARD', 'MCQ', 'Margin of safety (coined by Benjamin Graham) means purchasing assets at a significant discount to intrinsic value.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 25 AND active = TRUE);

-- Level 26: Derivatives & Options
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A call option gives the buyer the right to:', 'Sell the underlying asset at the strike price', 'Buy the underlying asset at the strike price before expiry', 'Receive dividends from the underlying stock', 'Short-sell the underlying asset', 'B', 26, 'derivatives', 'HARD', 'MCQ', 'A call option gives the holder the right (not obligation) to buy the underlying asset at the agreed strike price before expiry.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 26 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Option premium is primarily affected by:', 'Dividend history only', 'Underlying price, time to expiry, volatility, and interest rates', 'The option buyer\'s credit score', 'Stock split history', 'B', 26, 'derivatives', 'HARD', 'MCQ', 'Option pricing (Black-Scholes) uses underlying price, time to expiry, implied volatility, strike price, and risk-free rate.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 26 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Time decay (Theta) in options means:', 'Options gain value as expiry approaches', 'Option value decreases as expiry approaches, all else equal', 'The underlying stock loses value', 'Interest rates reduce option value', 'B', 26, 'derivatives', 'HARD', 'MCQ', 'Theta measures how much an option\'s value decreases each day as it approaches expiry. Options buyers lose to time decay.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 26 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A futures contract is different from an options contract because:', 'Futures involve no obligation', 'Futures obligate both parties to complete the transaction', 'Futures are only for commodities', 'Futures expire monthly only', 'B', 26, 'derivatives', 'HARD', 'MCQ', 'Futures create binding obligations for both buyer and seller; options give the buyer the right but not obligation.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 26 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Implied Volatility (IV) in options represents:', 'The historical price volatility of the stock', 'The market\'s expectation of future price volatility embedded in the option price', 'The volatility of interest rates', 'The standard deviation of earnings', 'B', 26, 'derivatives', 'HARD', 'MCQ', 'IV is derived from option market prices and reflects the market\'s consensus expectation of future volatility.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 26 AND active = TRUE);

-- Level 27: Alternative Investments
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Private equity (PE) investing typically involves:', 'Buying publicly listed shares', 'Investing in private companies with a long-term hold and value creation plan', 'Short-term currency trading', 'Government bond auctions', 'B', 27, 'alternatives', 'HARD', 'MCQ', 'PE firms invest in private companies, restructure them, and exit after 5-7 years via IPO or sale for returns.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 27 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Hedge funds primarily aim to:', 'Outperform the stock market in all conditions using complex strategies', 'Track a benchmark index', 'Offer guaranteed returns', 'Invest only in government bonds', 'A', 27, 'alternatives', 'HARD', 'MCQ', 'Hedge funds use strategies like long/short equity, arbitrage, and derivatives to generate absolute returns in all market conditions.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 27 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Infrastructure Investment Trusts (InvITs) are similar to REITs but invest in:', 'Stock portfolios', 'Infrastructure assets like roads, pipelines, and power transmission', 'Commercial real estate only', 'Government bonds', 'B', 27, 'alternatives', 'HARD', 'MCQ', 'InvITs pool investor capital to own and operate infrastructure projects, distributing income as dividends.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 27 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Venture capital (VC) investing focuses on:', 'Mature companies with steady cash flows', 'Early-stage, high-growth potential startups', 'Government infrastructure projects', 'Distressed debt restructuring', 'B', 27, 'alternatives', 'HARD', 'MCQ', 'VC funds invest in early-stage startups with high growth potential, accepting high risk for potentially high returns.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 27 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Commodity investing in gold helps a portfolio by:', 'Providing equity-like returns', 'Acting as a hedge against inflation and currency devaluation', 'Eliminating all portfolio risk', 'Guaranteeing annual returns', 'B', 27, 'alternatives', 'HARD', 'MCQ', 'Gold historically preserves purchasing power during inflation and acts as a safe haven during economic stress.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 27 AND active = TRUE);

-- Level 28: Advanced Risk Management
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Value at Risk (VaR) measures:', 'The maximum possible loss ever', 'The maximum expected loss over a given time period at a given confidence level', 'Average daily return', 'Correlation between assets', 'B', 28, 'risk-management', 'HARD', 'MCQ', 'VaR estimates the worst expected loss over a specific period at a given confidence level (e.g., 95% VaR = 5% chance of exceeding this loss).', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 28 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Hedging a portfolio means:', 'Removing all risk', 'Taking an offsetting position to reduce exposure to adverse price movements', 'Investing only in bonds', 'Diversifying into 50+ stocks', 'B', 28, 'risk-management', 'HARD', 'MCQ', 'Hedging uses instruments like futures, options, or inverse ETFs to offset potential losses in a portfolio.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 28 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Counterparty risk in derivatives refers to:', 'Market price risk', 'The risk that the other party in a contract defaults on their obligation', 'Interest rate sensitivity', 'Liquidity risk of the underlying', 'B', 28, 'risk-management', 'HARD', 'MCQ', 'Counterparty risk is the probability that the opposing party in a financial contract will fail to fulfil their obligations.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 28 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Black Swan events in financial markets are characterised by:', 'High probability and high impact', 'Low probability, extreme impact, and are rationalised in hindsight', 'Normal distribution outcomes', 'Events that are always predictable', 'B', 28, 'risk-management', 'HARD', 'MCQ', 'Black Swan events (Taleb) are rare, extreme-impact, unpredictable events — e.g., 2008 crisis, COVID-19 crash.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 28 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Liquidity risk is the risk that:', 'An investment loses value due to inflation', 'An asset cannot be sold quickly enough at a fair price', 'Interest rates rise unexpectedly', 'The company goes bankrupt', 'B', 28, 'risk-management', 'HARD', 'MCQ', 'Liquidity risk arises when you cannot exit a position quickly without significantly impacting the price.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 28 AND active = TRUE);

-- Level 29: Corporate Finance Fundamentals
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'WACC (Weighted Average Cost of Capital) is used in:', 'Personal budgeting', 'Determining the discount rate for company valuation', 'Calculating mutual fund NAV', 'Measuring portfolio beta', 'B', 29, 'corporate-finance', 'HARD', 'MCQ', 'WACC blends the cost of equity and debt weighted by their proportions in the capital structure, used to discount FCF in DCF models.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 29 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Debt-to-Equity (D/E) ratio for a company measures:', 'Profitability relative to revenue', 'Financial leverage — how much debt is used relative to equity', 'Return generated per unit of equity', 'Cash flow coverage of interest', 'B', 29, 'corporate-finance', 'HARD', 'MCQ', 'D/E = Total Debt / Total Equity. A high D/E indicates high leverage and financial risk.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 29 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Earnings Per Share (EPS) is calculated as:', 'Revenue ÷ total shares outstanding', 'Net income ÷ weighted average shares outstanding', 'Operating profit ÷ equity', 'EBITDA ÷ total shares', 'B', 29, 'corporate-finance', 'HARD', 'MCQ', 'EPS = Net Income / Weighted Average Shares Outstanding. It measures profitability per share.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 29 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A rights issue by a company involves:', 'Offering new shares to the public at market price', 'Offering existing shareholders the right to buy new shares at a discounted price', 'Buying back shares from the market', 'Issuing bonds convertible to equity', 'B', 29, 'corporate-finance', 'HARD', 'MCQ', 'A rights issue lets existing shareholders buy new shares at a discounted price, maintaining their proportional ownership.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 29 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Interest Coverage Ratio = EBIT ÷ Interest Expense. A ratio of 1.5 means:', 'The company easily covers interest', 'The company earns only 50% more than needed to pay interest — financially stressed', 'The company has no debt', 'The company is highly profitable', 'B', 29, 'corporate-finance', 'HARD', 'MCQ', 'An interest coverage of 1.5 is dangerously low — most analysts want to see above 3× for financial comfort.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 29 AND active = TRUE);

-- Level 30: Macroeconomics & Markets
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'When RBI raises the repo rate, the expected effect is:', 'Lower borrowing costs for banks', 'Higher borrowing costs, reducing money supply and controlling inflation', 'Increased stock market returns', 'Lower bond yields', 'B', 30, 'macroeconomics', 'HARD', 'MCQ', 'Higher repo rates increase the cost of borrowing for banks, which pass it on — slowing credit growth and inflation.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 30 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'An inverted yield curve (short rates > long rates) is historically associated with:', 'Economic expansion', 'Potential recession', 'High inflation', 'Currency appreciation', 'B', 30, 'macroeconomics', 'HARD', 'MCQ', 'An inverted yield curve has preceded most US recessions — it signals market expectations of future rate cuts and slowdown.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 30 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Quantitative Easing (QE) by a central bank involves:', 'Raising interest rates sharply', 'Buying government securities to inject liquidity into the financial system', 'Reducing money supply', 'Increasing bank reserve requirements', 'B', 30, 'macroeconomics', 'HARD', 'MCQ', 'QE involves central banks purchasing assets (bonds) to increase money supply and stimulate a sluggish economy.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 30 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'India\'s GDP growth is most closely related to which index for stock markets?', 'Real GDP growth directly predicts Nifty returns', 'Nominal GDP growth is one of many factors; corporate earnings growth drives markets more directly', 'GDP and markets move in opposite directions', 'GDP affects only bond markets', 'B', 30, 'macroeconomics', 'HARD', 'MCQ', 'While GDP growth supports corporate revenue growth, markets also factor in earnings quality, valuations, and global sentiment.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 30 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A Current Account Deficit (CAD) means:', 'The government spent more than tax revenues', 'A country imports more goods and services than it exports', 'Forex reserves are falling', 'Bond yields are rising', 'B', 30, 'macroeconomics', 'HARD', 'MCQ', 'CAD occurs when a country\'s imports of goods, services, and transfers exceed its exports, creating a net outflow.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 30 AND active = TRUE);

-- Level 31: International Investing
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Investing in US stocks from India exposes you to:', 'Only market risk', 'Both market risk and currency exchange rate risk (INR/USD)', 'No additional risk', 'Only inflation risk', 'B', 31, 'international', 'HARD', 'MCQ', 'International investing adds currency risk — if INR appreciates vs USD, your USD-denominated returns reduce in INR terms.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 31 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Under RBI\'s Liberalised Remittance Scheme (LRS), Indians can remit up to per year:', 'USD 50,000', 'USD 100,000', 'USD 250,000', 'USD 1,000,000', 'C', 31, 'international', 'HARD', 'MCQ', 'LRS allows Indian residents to remit up to USD 250,000 per financial year for permitted transactions including investments abroad.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 31 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'An ETF that tracks the S&P 500 gives Indian investors:', 'Direct stock picking in US markets', 'Broad exposure to top 500 US companies through a single instrument', 'Currency-hedged exposure only', 'Exposure to Indian IT exports', 'B', 31, 'international', 'HARD', 'MCQ', 'An S&P 500 ETF (listed in India or via LRS) provides diversified exposure to 500 large US companies across sectors.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 31 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Why do developed market bonds (like US Treasuries) offer lower yields?', 'Investors prefer higher returns', 'Lower default risk justifies lower yields', 'Central banks mandate low rates globally', 'They have shorter durations', 'B', 31, 'international', 'HARD', 'MCQ', 'Sovereign bonds from stable economies (US, Germany) offer lower yields because of near-zero default risk.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 31 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Country risk in international investing includes:', 'Market volatility only', 'Political instability, regulatory changes, and currency inconvertibility risks', 'Only forex risk', 'Only inflation risk', 'B', 31, 'international', 'HARD', 'MCQ', 'Country risk encompasses political, regulatory, economic instability, and the ability to convert/repatriate funds.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 31 AND active = TRUE);

-- Level 32: Advanced Tax Strategies
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A family trust can be used in estate planning to:', 'Eliminate all taxes permanently', 'Distribute assets to beneficiaries efficiently while potentially reducing estate taxes', 'Avoid filing income tax returns', 'Invest only in government bonds', 'B', 32, 'advanced-tax', 'HARD', 'MCQ', 'Family trusts can help in controlled asset distribution, succession planning, and may reduce estate/gift tax exposure.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 32 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Indexation benefit on debt fund redemption (before Apr 2023 change) allowed investors to:', 'Deduct 30% tax always', 'Inflate the purchase cost with inflation index, reducing taxable capital gains', 'Avoid paying any tax on debt funds', 'Apply 10% flat tax without indexation', 'B', 32, 'advanced-tax', 'HARD', 'MCQ', 'Indexation used CII (Cost Inflation Index) to increase the effective purchase price, reducing LTCG on debt funds.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 32 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'HUF (Hindu Undivided Family) as a tax entity helps because:', 'It is exempt from all taxes', 'It has its own PAN and basic exemption slab, allowing income splitting within families', 'HUF members pay no tax', 'Only applicable for NRI families', 'B', 32, 'advanced-tax', 'HARD', 'MCQ', 'An HUF has its own PAN and income tax slab benefits, allowing families to legitimately split income across the HUF entity.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 32 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Section 54 of the Income Tax Act allows capital gains tax exemption when:', 'You invest in ELSS', 'Long-term capital gains from sale of a house are reinvested in another house property', 'You deposit gains in a bank', 'You donate to charity', 'B', 32, 'advanced-tax', 'HARD', 'MCQ', 'Section 54 provides LTCG exemption on sale of residential property if the gains are reinvested in another residential property.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 32 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Tax incidence versus tax burden — tax incidence refers to:', 'Who legally pays the tax', 'Who ultimately bears the economic burden of the tax after market adjustments', 'The tax rate applied', 'The tax filing deadline', 'B', 32, 'advanced-tax', 'HARD', 'MCQ', 'Tax incidence studies who bears the real economic burden — sellers may legally pay but pass the cost to buyers through price changes.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 32 AND active = TRUE);

-- Level 33: Wealth Management
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A comprehensive wealth management plan includes:', 'Only investments', 'Investments, tax planning, estate planning, insurance, and retirement planning', 'Only retirement savings', 'Only insurance', 'B', 33, 'wealth-management', 'HARD', 'MCQ', 'Wealth management is holistic — it integrates all aspects of a client\'s financial life into a coordinated strategy.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 33 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A fee-only financial advisor is preferred over a commission-based one because:', 'They charge higher fees', 'They have no financial incentive to recommend products — purely act in client\'s interest', 'They only advise on insurance', 'They are regulated by AMFI', 'B', 33, 'wealth-management', 'HARD', 'MCQ', 'Fee-only advisors charge flat fees/hourly rates, eliminating conflicts of interest from product commissions.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 33 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Estate planning is primarily concerned with:', 'Maximising stock returns', 'Ensuring your assets are distributed according to your wishes after death, with minimal tax and delay', 'Filing annual tax returns', 'Buying life insurance only', 'B', 33, 'wealth-management', 'HARD', 'MCQ', 'Estate planning involves wills, trusts, nominations, and legal structures to ensure smooth, tax-efficient asset transfer.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 33 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Succession planning in family businesses ensures:', 'The business is sold immediately', 'A structured transition of leadership and ownership to the next generation or successors', 'Employees inherit the business', 'The bank takes control on retirement', 'B', 33, 'wealth-management', 'HARD', 'MCQ', 'Succession planning identifies and develops future leaders, ensuring business continuity and protecting family wealth.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 33 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Impact investing aims to:', 'Maximise financial returns only', 'Generate measurable social/environmental impact alongside financial returns', 'Invest in government bonds only', 'Fund charitable donations with no expected return', 'B', 33, 'wealth-management', 'HARD', 'MCQ', 'Impact investing intentionally creates positive social or environmental outcomes while also generating financial returns.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 33 AND active = TRUE);

-- Level 34: Financial Innovation & Fintech
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'UPI (Unified Payments Interface) enables:', 'Physical cash transactions only', 'Instant inter-bank fund transfers 24/7 using a virtual payment address', 'International wire transfers only', 'Credit card payments only', 'B', 34, 'fintech', 'HARD', 'MCQ', 'UPI enables real-time, round-the-clock fund transfers between bank accounts via smartphones using a virtual address (VPA).', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 34 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Account Aggregator (AA) framework in India allows:', 'Banks to share data without consent', 'Consumers to share financial data securely across institutions with consent', 'Only government agencies to access financial data', 'Automatic investment based on transactions', 'B', 34, 'fintech', 'HARD', 'MCQ', 'AA framework is an RBI-regulated consent-based data-sharing system that lets users share financial data securely across institutions.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 34 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Robo-advisors in wealth management use:', 'Human advisors for all decisions', 'Algorithms to create and manage diversified portfolios based on goals and risk tolerance', 'Only government bonds', 'Insider information', 'B', 34, 'fintech', 'HARD', 'MCQ', 'Robo-advisors automate portfolio construction and rebalancing using algorithms, lowering cost and improving accessibility.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 34 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Open Banking allows:', 'Centralised government control of banking', 'Third-party fintech apps to access bank data (with consent) to build financial products', 'Banks to share data freely without user consent', 'Only public sector banks to offer digital services', 'B', 34, 'fintech', 'HARD', 'MCQ', 'Open Banking uses APIs to allow authorised third-party applications to access consumer banking data with explicit consent.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 34 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Buy Now Pay Later (BNPL) services present a financial risk because:', 'They offer too much interest income', 'They may encourage overspending and create hidden debt burdens', 'They reduce credit scores automatically', 'They are only available for businesses', 'B', 34, 'fintech', 'HARD', 'MCQ', 'BNPL\'s ease of access can lead to impulse purchases and accumulation of multiple deferred payment obligations.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 34 AND active = TRUE);

-- Level 35: ESG Investing & Sustainable Finance
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'ESG in investing stands for:', 'Equity, Stocks, Gains', 'Environmental, Social, Governance', 'Emerging, Stable, Growth', 'Earnings, Sentiment, Growth', 'B', 35, 'esg', 'HARD', 'MCQ', 'ESG (Environmental, Social, Governance) factors are non-financial criteria used to evaluate a company\'s sustainability and ethical impact.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 35 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Greenwashing in ESG investing refers to:', 'Genuinely sustainable business practices', 'Companies misleadingly presenting themselves as environmentally responsible', 'Green energy investments', 'ESG fund reporting', 'B', 35, 'esg', 'HARD', 'MCQ', 'Greenwashing is when companies exaggerate or falsely claim environmental credentials to appear more sustainable than they are.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 35 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Green bonds are issued to finance:', 'Any government project', 'Projects with positive environmental impact (renewable energy, clean transport, etc.)', 'Corporate dividend payments', 'Stock buyback programmes', 'B', 35, 'esg', 'HARD', 'MCQ', 'Green bonds are fixed-income instruments specifically used to finance or refinance projects with environmental benefits.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 35 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The governance (G) pillar in ESG evaluates:', 'A company\'s emissions', 'Board composition, executive pay, shareholder rights, and business ethics', 'Employee welfare programs', 'Carbon footprint only', 'B', 35, 'esg', 'HARD', 'MCQ', 'Governance examines leadership structure, board independence, executive compensation, transparency, and anti-corruption policies.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 35 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Research on ESG funds vs. non-ESG funds shows:', 'ESG funds always outperform', 'ESG funds generally show competitive returns with lower volatility and less scandal risk', 'ESG funds always underperform', 'No difference in performance', 'B', 35, 'esg', 'HARD', 'MCQ', 'Studies suggest ESG funds often match or outperform over the long term, with lower tail risk from governance scandals.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 35 AND active = TRUE);

-- Level 36: Advanced Finance Mastery Challenge
INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Modigliani-Miller theorem states (in a perfect market):', 'Capital structure determines firm value', 'Firm value is independent of capital structure (debt/equity mix)', 'Dividends always increase firm value', 'Leverage always lowers cost of capital', 'B', 36, 'advanced-mastery', 'HARD', 'MCQ', 'Modigliani-Miller: In a frictionless market, a firm\'s value is unaffected by how it is financed — only assets matter.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 36 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The Black-Scholes model is used for:', 'Calculating bond YTM', 'Pricing European-style options on non-dividend paying stocks', 'Valuing real estate', 'Computing DCF of a company', 'B', 36, 'advanced-mastery', 'HARD', 'MCQ', 'Black-Scholes prices European options using spot price, strike, time to expiry, risk-free rate, and volatility (sigma).', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 36 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'A Ponzi scheme collapses because:', 'Regulators detect it quickly', 'It relies on new investor money to pay earlier investors, becoming unsustainable', 'Returns are too low to attract investors', 'The underlying investments lose all value', 'B', 36, 'advanced-mastery', 'HARD', 'MCQ', 'Ponzi schemes pay existing investors with capital from new investors — they collapse when new money cannot support payouts.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 36 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'Modern Portfolio Theory (MPT) was developed by:', 'Warren Buffett', 'Harry Markowitz', 'Benjamin Graham', 'Eugene Fama', 'B', 36, 'advanced-mastery', 'HARD', 'MCQ', 'Harry Markowitz introduced MPT in 1952, showing how combining assets with different correlations can maximise return for a given risk level.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 36 AND active = TRUE);

INSERT INTO quiz_questions (question, option_a, option_b, option_c, option_d, correct_answer, level, topic, difficulty, question_type, explanation, active)
SELECT 'The concept of "time value of money" underlies which core financial principle?', 'A rupee today is worth the same as a rupee tomorrow', 'A rupee today is worth more than a rupee tomorrow because it can be invested to earn returns', 'Future money is always worth more than present money', 'Money has no intrinsic time value', 'B', 36, 'advanced-mastery', 'HARD', 'MCQ', 'TVM is the foundational principle: present money can earn returns, making ₹1 today worth more than ₹1 in the future.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM quiz_questions WHERE level = 36 AND active = TRUE);
