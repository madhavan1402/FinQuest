package com.finquest.service;

import com.finquest.dto.*;
import com.finquest.model.*;
import com.finquest.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Map.entry;

/**
 * LearningPathService — owns everything about the LEARNING PATH (levels,
 * tiers, unlock rules, per-level progress). It is COMPLETELY SEPARATE from the
 * user's GAMIFICATION level (XP/level/streak/badges), which is handled solely
 * by {@link GamificationService}.
 *
 * Responsibilities:
 *   • Seed / maintain the 36-level curriculum (idempotent, preserves IDs)
 *   • Build the tiered learning-path response (BEGINNER/INTERMEDIATE/ADVANCED)
 *   • Return secure quiz questions (NO correctAnswer leaked)
 *   • Score submissions on the backend and decide pass/fail
 *   • Track attempts, best score, last score, status
 *   • Unlock the next level after a pass
 *   • Delegate ALL XP/coin/streak/achievement/badge rewards to GamificationService
 */
@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final UserRepository          users;
    private final LearningModuleRepository modules;
    private final UserProgressRepository  progress;
    private final QuizRepository          quizRepository;
    private final QuizResultRepository    quizResultRepository;
    private final GamificationService     gamification;
    private final UserAchievementRepository userAchievements;

    @Value("${quiz.passing-percentage:70}")
    private int passingPercentage;

    // ── Curriculum definition (36 levels) ────────────────────────────────────
    // Only NEW modules listed here are inserted. The 15 pre-existing modules
    // (which already have rows + preserved IDs) are remapped in V5 migration
    // and simply re-synced here.
    private record LevelSeed(int seq, String key, String title, LearningTier tier,
                             Difficulty difficulty, String description, int xp, int coins, int minutes) {}

    private static final List<LevelSeed> CURRICULUM = List.of(
        // ── BEGINNER 1-12 ────────────────────────────────────────────────────
        new LevelSeed(1,  "money-basics",            "Money Basics",             LearningTier.BEGINNER,     Difficulty.EASY,   "Learn what money is, income, expenses, and the difference between needs and wants.",             100, 25, 10),
        new LevelSeed(2,  "budgeting-basics",        "Budgeting Basics",         LearningTier.BEGINNER,     Difficulty.EASY,   "Track income and expenses, build a basic budget, and apply the 50/30/20 concept.",                105, 26, 12),
        new LevelSeed(3,  "saving-money",            "Saving Money",             LearningTier.BEGINNER,     Difficulty.EASY,   "Build saving habits, set saving goals, pay yourself first, and grow short-term savings.",         110, 27, 14),
        new LevelSeed(4,  "emergency-fund",          "Emergency Fund",           LearningTier.BEGINNER,     Difficulty.EASY,   "Understand emergency funds, their purpose, liquidity, and handling unexpected expenses.",         115, 28, 15),
        new LevelSeed(5,  "banking-basics",          "Banking Basics",           LearningTier.BEGINNER,     Difficulty.EASY,   "Learn savings and current accounts, interest, bank statements, and basic banking terms.",         120, 29, 16),
        new LevelSeed(6,  "digital-payments",        "Digital Payments",         LearningTier.BEGINNER,     Difficulty.EASY,   "Use UPI, debit cards and online payments safely, and stay aware of payment fraud.",               125, 30, 18),
        new LevelSeed(7,  "credit-score-basics",     "Credit Score Basics",      LearningTier.BEGINNER,     Difficulty.EASY,   "Understand credit scores, credit history, the factors that affect them, and responsible usage.",   130, 31, 18),
        new LevelSeed(8,  "loans-basics",            "Loans Basics",             LearningTier.BEGINNER,     Difficulty.EASY,   "Learn about principal, interest, EMI, tenure, and secured vs unsecured loans.",                    135, 32, 20),
        new LevelSeed(9,  "good-vs-bad-debt",        "Good Debt vs Bad Debt",    LearningTier.BEGINNER,     Difficulty.EASY,   "Learn productive debt, high-cost debt, debt management and the debt-to-income concept.",           140, 33, 12),
        new LevelSeed(10, "financial-goals",         "Financial Goals",          LearningTier.BEGINNER,     Difficulty.EASY,   "Set short, medium and long-term goals using the SMART framework.",                                145, 34, 12),
        new LevelSeed(11, "personal-cash-flow",      "Personal Cash Flow",       LearningTier.BEGINNER,     Difficulty.EASY,   "Track income, fixed and variable expenses, and manage your savings rate.",                        150, 35, 13),
        new LevelSeed(12, "beginner-challenge",      "Beginner Finance Challenge", LearningTier.BEGINNER,   Difficulty.MEDIUM, "Mixed questions covering all Beginner levels 1-11.",                                               200, 45, 15),

        // ── INTERMEDIATE 13-24 ───────────────────────────────────────────────
        new LevelSeed(13, "investing-basics",        "Investing Basics",         LearningTier.INTERMEDIATE, Difficulty.EASY,   "Learn how investing works, the basic building blocks of wealth creation, and risk-return trade-offs.", 155, 36, 20),
        new LevelSeed(14, "stock-market-basics",     "Stock Market Basics",      LearningTier.INTERMEDIATE, Difficulty.EASY,   "Understand how the stock market works, indices, and long-term investing thinking.",                 160, 37, 22),
        new LevelSeed(15, "mutual-funds",            "Mutual Funds",             LearningTier.INTERMEDIATE, Difficulty.EASY,   "See how diversified mutual funds can match different financial goals.",                            165, 38, 22),
        new LevelSeed(16, "sip-compound-interest",   "SIP & Compound Interest",  LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Understand systematic investment plans and the power of compound interest.",                       170, 40, 18),
        new LevelSeed(17, "bonds-fixed-income",      "Bonds & Fixed Income",     LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Explore bonds, fixed deposits and other fixed-income instruments.",                                175, 41, 18),
        new LevelSeed(18, "risk-diversification",    "Risk & Diversification",   LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Learn how risk and diversification protect your portfolio.",                                       180, 42, 19),
        new LevelSeed(19, "portfolio-building",      "Portfolio Building",       LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Combine assets into a cohesive investment portfolio.",                                             185, 43, 19),
        new LevelSeed(20, "tax-basics",              "Tax Basics",               LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Learn income tax basics, deductions, returns, and the importance of tax planning.",                 190, 44, 25),
        new LevelSeed(21, "insurance-planning",      "Insurance Planning",       LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Get familiar with health, life and motor insurance, and how to plan your coverage.",               195, 45, 20),
        new LevelSeed(22, "inflation-purchasing",    "Inflation & Purchasing Power", LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Understand how inflation erodes purchasing power over time.",                                      200, 46, 20),
        new LevelSeed(23, "financial-planning",      "Financial Planning",       LearningTier.INTERMEDIATE, Difficulty.MEDIUM, "Build a complete personal financial plan for your goals.",                                         205, 47, 20),
        new LevelSeed(24, "intermediate-challenge",  "Intermediate Finance Challenge", LearningTier.INTERMEDIATE, Difficulty.HARD, "Mixed questions covering all Intermediate levels 13-23.",                                          250, 55, 22),

        // ── ADVANCED 25-36 ───────────────────────────────────────────────────
        new LevelSeed(25, "advanced-investing",      "Advanced Investing",       LearningTier.ADVANCED,     Difficulty.MEDIUM, "Advanced strategies for growing substantial wealth over time.",                                    210, 48, 24),
        new LevelSeed(26, "equity-analysis",         "Equity Analysis",          LearningTier.ADVANCED,     Difficulty.MEDIUM, "Analyse companies and equity instruments for better decisions.",                                   215, 49, 24),
        new LevelSeed(27, "fundamental-analysis",    "Fundamental Analysis",     LearningTier.ADVANCED,     Difficulty.HARD,   "Evaluate companies using financial statements and ratios.",                                        220, 50, 25),
        new LevelSeed(28, "technical-analysis",      "Technical Analysis Basics",LearningTier.ADVANCED,     Difficulty.HARD,   "Read price charts, trends and indicators for trading decisions.",                                  225, 51, 25),
        new LevelSeed(29, "portfolio-optimization",  "Portfolio Optimization",   LearningTier.ADVANCED,     Difficulty.HARD,   "Optimise your portfolio for maximum return per unit of risk.",                                     230, 52, 26),
        new LevelSeed(30, "asset-allocation",        "Asset Allocation",         LearningTier.ADVANCED,     Difficulty.HARD,   "Distribute assets strategically across classes based on goals and risk.",                          235, 53, 26),
        new LevelSeed(31, "tax-optimization",        "Tax Optimization",         LearningTier.ADVANCED,     Difficulty.HARD,   "Use deductions and investments to legally minimise your tax burden.",                              240, 54, 27),
        new LevelSeed(32, "retirement-planning",     "Retirement Planning",      LearningTier.ADVANCED,     Difficulty.HARD,   "Build a long-term retirement strategy with pension and savings options.",                          245, 55, 28),
        new LevelSeed(33, "wealth-management",       "Wealth Management",        LearningTier.ADVANCED,     Difficulty.HARD,   "Comprehensive strategies for managing and growing wealth.",                                       250, 56, 28),
        new LevelSeed(34, "passive-income",          "Passive Income",           LearningTier.ADVANCED,     Difficulty.HARD,   "Build income streams that require little ongoing effort.",                                         255, 57, 28),
        new LevelSeed(35, "financial-freedom",       "Financial Independence",   LearningTier.ADVANCED,     Difficulty.HARD,   "Build passive income streams and achieve financial independence.",                                 260, 58, 30),
        new LevelSeed(36, "advanced-challenge",      "Advanced Finance Challenge", LearningTier.ADVANCED,   Difficulty.HARD,   "Mixed questions covering all Advanced levels 25-35.",                                              300, 70, 30)
    );

// Map of OLD module keys (already in DB) → new curriculum key/title
    // so existing rows are UPDATED in place, preserving their IDs.
    private static final Map<String, String> OLD_KEY_TO_NEW_KEY = Map.ofEntries(
        entry("introduction-finance", "money-basics"),
        entry("budgeting-basics",     "budgeting-basics"),
        entry("saving-money",         "saving-money"),
        entry("emergency-fund",       "emergency-fund"),
        entry("banking",              "banking-basics"),
        entry("digital-payments",     "digital-payments"),
        entry("credit-score",         "credit-score-basics"),
        entry("loans",                "loans-basics"),
        entry("investing-basics",     "investing-basics"),
        entry("stock-market",         "stock-market-basics"),
        entry("mutual-funds",         "mutual-funds"),
        entry("taxes",                "tax-basics"),
        entry("insurance",            "insurance-planning"),
        entry("retirement-planning",  "retirement-planning"),
        entry("financial-freedom",    "financial-freedom")
    );

    // ── Idempotent curriculum seeding ───────────────────────────────────────
    @PostConstruct
    void seedModules() {
        for (LevelSeed seed : CURRICULUM) {
            // 1. If a row already exists with this NEW key, update in place.
            Optional<LearningModule> existingByNewKey = modules.findByModuleKey(seed.key());
            if (existingByNewKey.isPresent()) {
                updateModule(existingByNewKey.get(), seed);
                continue;
            }
            // 2. If an OLD-keyed row exists that maps to this new key, reuse its ID.
            Optional<LearningModule> oldRow = OLD_KEY_TO_NEW_KEY.entrySet().stream()
                    .filter(e -> e.getValue().equals(seed.key()))
                    .map(e -> modules.findByModuleKey(e.getKey()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
            if (oldRow.isPresent()) {
                LearningModule m = oldRow.get();
                m.setModuleKey(seed.key());
                updateModule(m, seed);
                continue;
            }
            // 3. Otherwise create a brand-new row.
            LearningModule m = new LearningModule();
            m.setModuleKey(seed.key());
            updateModule(m, seed);
        }
    }

    private void updateModule(LearningModule m, LevelSeed seed) {
        m.setTitle(seed.title());
        m.setSequenceNumber(seed.seq());
        m.setTier(seed.tier());
        m.setDifficulty(seed.difficulty());
        m.setDescription(seed.description());
        m.setXpReward(seed.xp());
        m.setCoinReward(seed.coins());
        m.setEstimatedMinutes(seed.minutes());
        m.setActive(true);
        modules.save(m);
    }

    // ── Public API ──────────────────────────────────────────────────────────

    /** Full tiered learning path with user progress + summary. */
    @Transactional
    public TieredLearningPathDto getPath(Long userId) {
        User user = requireUser(userId);
        ensureProgress(user);
        List<LearningModule> all = modules.findByActiveTrueOrderBySequenceNumberAsc();
        Map<Long, UserProgress> entries = new HashMap<>();
        progress.findByUserId(userId).forEach(p -> entries.put(p.getLearningModule().getId(), p));

        eduLevel(user, entries); // recompute statuses based on completion

        List<LevelDto> beginner = new ArrayList<>();
        List<LevelDto> intermediate = new ArrayList<>();
        List<LevelDto> advanced = new ArrayList<>();
        for (LearningModule m : all) {
            LevelDto dto = toLevelDto(m, entries.get(m.getId()));
            switch (m.getTier()) {
                case BEGINNER -> beginner.add(dto);
                case INTERMEDIATE -> intermediate.add(dto);
                case ADVANCED -> advanced.add(dto);
            }
        }

        List<TierDto> tiers = List.of(
                new TierDto(LearningTier.BEGINNER, "Beginner", beginner),
                new TierDto(LearningTier.INTERMEDIATE, "Intermediate", intermediate),
                new TierDto(LearningTier.ADVANCED, "Advanced", advanced)
        );

        long completed = entries.values().stream().filter(UserProgress::isCompleted).count();
        return new TieredLearningPathDto(tiers, summary(user, completed, all.size()));
    }

    /** Detailed info for a single level. */
    @Transactional
    public LevelDetailDto getLevelDetail(Long userId, int levelNumber) {
        User user = requireUser(userId);
        ensureProgress(user);
        LearningModule module = modules.findByActiveTrueOrderBySequenceNumberAsc().stream()
                .filter(m -> m.getSequenceNumber() == levelNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Level not found: " + levelNumber));
        UserProgress p = progress.findByUserIdAndLearningModuleId(user.getId(), module.getId())
                .orElseThrow();
        long questionCount = quizRepository.countBylevelAndActiveTrue(levelNumber);
        return new LevelDetailDto(
                module.getSequenceNumber(), module.getModuleKey(), module.getTitle(),
                module.getDescription(), module.getTier().name(), module.getDifficulty(),
                (int) questionCount, module.getXpReward(), module.getCoinReward(),
                module.getEstimatedMinutes(), p.getStatus(), p.getAttempts(), p.getBestScore());
    }

    /** Secure quiz questions — correctAnswer is NEVER returned. */
    @Transactional(readOnly = true)
    public List<QuizQuestionDto> getLevelQuiz(Long userId, int levelNumber) {
        requireUser(userId);
        List<QuizQuestion> questions = quizRepository.findBylevelAndActiveTrue(levelNumber);
        List<QuizQuestionDto> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            result.add(new QuizQuestionDto(
                    q.getId(), i + 1, q.getQuestion(),
                    List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                    q.getDifficulty(), q.getTopic()));
        }
        return result;
    }

    /** Backend-scored submission. Decides pass/fail, updates progress, unlocks next. */
    @Transactional
    public LevelQuizResultDto submitLevelQuiz(Long userId, int levelNumber, Map<String, String> answers) {
        User user = requireUser(userId);
        ensureProgress(user);

        LearningModule module = modules.findByActiveTrueOrderBySequenceNumberAsc().stream()
                .filter(m -> m.getSequenceNumber() == levelNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Level not found: " + levelNumber));

        UserProgress current = progress.findByUserIdAndLearningModuleId(userId, module.getId())
                .orElseThrow();

        // A locked level cannot be attempted.
        if (current.getStatus() == LevelStatus.LOCKED) {
            throw new IllegalStateException("Complete the previous level to unlock level " + levelNumber);
        }

        List<QuizQuestion> questions = quizRepository.findBylevelAndActiveTrue(levelNumber);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("No active questions for level " + levelNumber);
        }

        // Score on the backend.
        int correct = 0;
        for (QuizQuestion q : questions) {
            String submitted = answers.get(String.valueOf(q.getId()));
            if (q.getCorrectAnswer().equalsIgnoreCase(submitted)) correct++;
        }
        int total = questions.size();
        int percentage = (int) Math.round(correct * 100.0 / total);
        boolean passed = percentage >= passingPercentage;
        boolean perfect = correct == total;

        // Persist the quiz result row (preserves attempt history).
        quizResultRepository.save(new QuizResult(null, user, levelNumber, correct));

        // Update progress tracking (attempts, best score, last score, status).
        current.setAttempts(current.getAttempts() + 1);
        current.setLastScore(percentage);
        current.setCorrectAnswers(correct);
        current.setQuestionsAnswered(total);
        current.setLastAttemptAt(LocalDateTime.now());
        if (percentage > current.getBestScore()) current.setBestScore(percentage);
        if (current.getStartedAt() == null) current.setStartedAt(LocalDateTime.now());

        boolean nextUnlocked = false;
        boolean newlyCompleted = false;

        if (passed && !current.isCompleted()) {
            newlyCompleted = true;
            current.setCompleted(true);
            current.setCompletedAt(LocalDateTime.now());
            current.setStatus(LevelStatus.COMPLETED);
            progress.save(current);

            // Unlock the next level.
            nextUnlocked = unlockNext(user, module.getSequenceNumber());
        } else if (current.isCompleted()) {
            current.setStatus(LevelStatus.COMPLETED);
        } else {
            current.setStatus(current.getAttempts() > 0 ? LevelStatus.IN_PROGRESS : LevelStatus.UNLOCKED);
        }
        progress.save(current);

// ── Gamification Integration (delegated; no duplication) ────────────
        // Only award XP/coins/streak/badges on the FIRST completion of a level.
        GamificationService.LevelUpResult levelUp = null;
        GamificationService.StreakResult streak = null;
        List<AchievementDto> newAchievements = new ArrayList<>();
        List<BadgeDto> newBadges = new ArrayList<>();
        int xpEarned = 0;
        int coinsEarned = 0;

        if (newlyCompleted) {
            xpEarned = module.getXpReward();
            coinsEarned = module.getCoinReward();
            levelUp = gamification.awardXpAndCoins(userId, xpEarned, coinsEarned,
                    "Completed level " + levelNumber + ": " + module.getTitle());
            streak = gamification.updateStreak(userId);

            // Module-specific achievements
            AchievementDto a;
            if ("saving-money".equals(module.getModuleKey())) {
                a = gamification.unlockAchievement(userId, "SAVING_MASTER");
                if (a != null) newAchievements.add(a);
            }
            if ("investing-basics".equals(module.getModuleKey())) {
                a = gamification.unlockAchievement(userId, "INVESTMENT_BEGINNER");
                if (a != null) newAchievements.add(a);
            }
            if ("money-basics".equals(module.getModuleKey())) {
                a = gamification.unlockAchievement(userId, "FIRST_LEVEL_COMPLETE");
                if (a != null) newAchievements.add(a);
            }

            // Finance Explorer — 5 levels completed
            long completedCount = progress.findByUserId(userId).stream()
                    .filter(UserProgress::isCompleted).count();
            if (completedCount >= 5) {
                a = gamification.unlockAchievement(userId, "FINANCE_EXPLORER");
                if (a != null) {
                    newAchievements.add(a);
                    BadgeDto b = gamification.unlockBadge(userId, "FINANCE_EXPLORER",
                            "Finance Explorer", "🏆");
                    if (b != null) newBadges.add(b);
                }
            }

            // Gamification level badges via processQuizResult-like helper
            newBadges.addAll(gamification.checkAndAwardBadges(
                    userId, gamification.levelFromTotalXp(requireUser(userId).getXp()),
                    streak == null ? 0 : streak.streak(), newAchievements));
        }

        // Reload user for final XP/level.
        User reloaded = requireUser(userId);
        int finalXp = reloaded.getXp();
        int finalLevel = reloaded.getLevel();
        int streakVal = streak == null ? reloaded.getLearningStreak() : streak.streak();

        return new LevelQuizResultDto(
                correct, total, percentage, passed,
                xpEarned, coinsEarned,
                finalXp, finalLevel,
                levelUp != null && levelUp.leveledUp(),
                GamificationService.xpToNextLevel(finalXp),
                streakVal,
                newAchievements, newBadges,
                newBadges.isEmpty() ? null : newBadges.get(0).getBadgeName(),
                nextUnlocked,
                getPath(userId));
    }

    /** Reset a user's learning progress only (does NOT touch XP/coins). */
    @Transactional
    public TieredLearningPathDto reset(Long userId) {
        User user = requireUser(userId);
        progress.deleteByUserId(user.getId());
        ensureProgress(user);
        return getPath(userId);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean unlockNext(User user, int currentSequenceNumber) {
        Optional<LearningModule> next = modules.findByActiveTrueOrderBySequenceNumberAsc().stream()
                .filter(m -> m.getSequenceNumber() == currentSequenceNumber + 1)
                .findFirst();
        if (next.isEmpty()) return false;
        UserProgress nextP = progress.findByUserIdAndLearningModuleId(user.getId(), next.get().getId())
                .orElseGet(() -> {
                    UserProgress np = new UserProgress();
                    np.setUser(user);
                    np.setLearningModule(next.get());
                    np.setUnlocked(false);
                    np.setCompleted(false);
                    np.setStatus(LevelStatus.LOCKED);
                    return progress.save(np);
                });
        if (!nextP.isUnlocked() && nextP.getStatus() == LevelStatus.LOCKED) {
            nextP.setUnlocked(true);
            nextP.setStatus(LevelStatus.UNLOCKED);
            progress.save(nextP);
        }
        return true;
    }

    private User requireUser(Long id) {
        return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Creates progress rows for any module the user does not yet have progress
     * for. NEVER resets or overwrites existing progress.
     * <p>
     * Unlock rules (only applied on initial row creation):
     * <ul>
     *   <li>BEGINNER  — Level 1 unlocked, all others locked.</li>
     *   <li>INTERMEDIATE — Levels 1-13 unlocked (entry point = Level 13), levels 14+ locked.</li>
     *   <li>ADVANCED  — Levels 1-25 unlocked (entry point = Level 25), levels 26+ locked.</li>
     * </ul>
     * No levels are marked completed by this method.
     */
    private void ensureProgress(User user) {
        List<LearningModule> all = modules.findByActiveTrueOrderBySequenceNumberAsc();

        // Determine unlock ceiling based on assessed literacy level
        String literacy = user.getLiteracyLevel();
        int unlockUpToSequence;
        if ("ADVANCED".equalsIgnoreCase(literacy)) {
            unlockUpToSequence = 25;
        } else if ("INTERMEDIATE".equalsIgnoreCase(literacy)) {
            unlockUpToSequence = 13;
        } else {
            unlockUpToSequence = 1; // BEGINNER (default)
        }

        for (LearningModule m : all) {
            if (progress.findByUserIdAndLearningModuleId(user.getId(), m.getId()).isPresent()) continue;
            UserProgress p = new UserProgress();
            p.setUser(user);
            p.setLearningModule(m);
            boolean unlocked = m.getSequenceNumber() <= unlockUpToSequence;
            p.setUnlocked(unlocked);
            p.setCompleted(false);
            p.setStatus(unlocked ? LevelStatus.UNLOCKED : LevelStatus.LOCKED);
            progress.save(p);
        }
    }

    /**
     * Reconciles status fields from the completed/unlocked booleans so the
     * DTO returns a consistent backend-derived status. Never changes the
     * underlying completion data.
     */
    private void eduLevel(User user, Map<Long, UserProgress> entries) {
        for (UserProgress p : entries.values()) {
            if (p.isCompleted()) {
                p.setStatus(LevelStatus.COMPLETED);
            } else if (p.isUnlocked()) {
                p.setStatus(p.getAttempts() > 0 ? LevelStatus.IN_PROGRESS : LevelStatus.UNLOCKED);
            } else {
                p.setStatus(LevelStatus.LOCKED);
            }
        }
    }

    private LevelDto toLevelDto(LearningModule m, UserProgress p) {
        UserProgress progress = p;
        if (progress == null) {
            progress = new UserProgress();
            progress.setStatus(LevelStatus.LOCKED);
            progress.setCompleted(false);
            progress.setBestScore(0);
        }
        return new LevelDto(
                m.getSequenceNumber(), m.getTitle(), m.getDescription(),
                m.getDifficulty(), m.getXpReward(), m.getCoinReward(),
                m.getEstimatedMinutes(), progress.getStatus(), progress.getBestScore(), progress.isCompleted());
    }

    private LearningPathSummaryDto summary(User u, long completed, int total) {
        return new LearningPathSummaryDto(
                Math.round(completed * 100.0 / Math.max(1, total)),
                u.getXp(), u.getLevel(), u.getCoins(), u.getLearningStreak(),
                completed, Math.max(0, total - completed));
    }
}
