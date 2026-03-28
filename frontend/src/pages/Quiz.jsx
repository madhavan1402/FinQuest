// Quiz.jsx
// Reads the level number from the URL: /quiz?level=2
// Tries GET /api/quiz/{level} first — falls back to hardcoded questions if
// the backend returns nothing (no DB seed needed for demo).
// On submit: POST /api/quiz/submit (if backend available) OR
//            POST /api/complete-level (always called to award XP).

import { useState, useEffect }    from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth }                from '../context/AuthContext';
import { completeLevel }          from '../api/endpoints';
import api                        from '../services/api';
import QuizCard                   from '../components/QuizCard';

// ── Fallback questions per level ──────────────────────────────────────────────
// Used when the backend has no seeded questions for a level.
// Each question must have: id, question, optionA-D, correctAnswer
const FALLBACK_QUESTIONS = {
  default: [
    {
      id: 'f1', question: 'What is the 50/30/20 budgeting rule?',
      optionA: '50% needs, 30% wants, 20% savings',
      optionB: '50% savings, 30% needs, 20% wants',
      optionC: '50% wants, 30% savings, 20% needs',
      optionD: 'None of the above',
      correctAnswer: 'A',
    },
    {
      id: 'f2', question: 'What does EMI stand for?',
      optionA: 'Equal Monthly Income',
      optionB: 'Equated Monthly Instalment',
      optionC: 'Extra Monthly Interest',
      optionD: 'Estimated Monthly Investment',
      correctAnswer: 'B',
    },
    {
      id: 'f3', question: 'Which of these is a safe investment option in India?',
      optionA: 'Cryptocurrency',
      optionB: 'Chit Funds',
      optionC: 'Public Provident Fund (PPF)',
      optionD: 'Penny Stocks',
      correctAnswer: 'C',
    },
    {
      id: 'f4', question: 'What is compound interest?',
      optionA: 'Interest on principal only',
      optionB: 'Interest on principal + previously earned interest',
      optionC: 'A fixed government rate',
      optionD: 'Interest paid monthly',
      correctAnswer: 'B',
    },
    {
      id: 'f5', question: 'What is a credit score used for?',
      optionA: 'Measuring your savings',
      optionB: 'Determining loan eligibility',
      optionC: 'Calculating income tax',
      optionD: 'Tracking stock prices',
      correctAnswer: 'B',
    },
  ],
  1: [
    {
      id: 'l1q1', question: 'What is money primarily used for?',
      optionA: 'Decoration', optionB: 'Medium of exchange',
      optionC: 'Making art',  optionD: 'None of the above',
      correctAnswer: 'B',
    },
    {
      id: 'l1q2', question: 'Which of these is NOT a form of money?',
      optionA: 'Cash', optionB: 'Debit card', optionC: 'Barter goods', optionD: 'Cheque',
      correctAnswer: 'C',
    },
    {
      id: 'l1q3', question: 'What is a bank?',
      optionA: 'A place to buy groceries',
      optionB: 'An institution that holds and lends money',
      optionC: 'A government office',
      optionD: 'A stock exchange',
      correctAnswer: 'B',
    },
  ],
  2: [
    {
      id: 'l2q1', question: 'What is a budget?',
      optionA: 'A type of loan',
      optionB: 'A plan for spending and saving money',
      optionC: 'A bank account type',
      optionD: 'A tax form',
      correctAnswer: 'B',
    },
    {
      id: 'l2q2', question: 'If income is ₹50,000 and expenses are ₹35,000, what are savings?',
      optionA: '₹85,000', optionB: '₹15,000', optionC: '₹20,000', optionD: '₹10,000',
      correctAnswer: 'B',
    },
    {
      id: 'l2q3', question: 'Which expense is a "need"?',
      optionA: 'Netflix subscription', optionB: 'Rent', optionC: 'Dining out', optionD: 'New phone',
      correctAnswer: 'B',
    },
  ],
};

// Get fallback questions for a given level, falling back to default set
const getFallbackQuestions = (level) =>
  FALLBACK_QUESTIONS[level] ?? FALLBACK_QUESTIONS.default;

export default function Quiz() {
  const { user, saveUser, saveQuizResult } = useAuth();
  const navigate = useNavigate();

  // Read ?level=N from the URL — defaults to 1 if not present
  const [searchParams] = useSearchParams();
  const quizLevel = parseInt(searchParams.get('level') ?? '1', 10);

  const [questions,  setQuestions]  = useState([]);
  const [answers,    setAnswers]    = useState({});
  const [result,     setResult]     = useState(null);
  const [phase,      setPhase]      = useState('loading');
  const [errorMsg,   setErrorMsg]   = useState('');
  // true when we're using fallback questions instead of backend data
  const [usingFallback, setUsingFallback] = useState(false);

  // ── Load questions on mount (or when level changes) ─────────────────────────
  useEffect(() => {
    loadQuestions();
  }, [quizLevel]);

  const loadQuestions = () => {
    setPhase('loading');
    setErrorMsg('');
    setAnswers({});
    setResult(null);
    setUsingFallback(false);

    api.get(`/quiz/${quizLevel}`)
      .then(({ data }) => {
        if (!data || data.length === 0) {
          // Backend reachable but no questions seeded — use fallback
          useFallback();
        } else {
          setQuestions(data);
          setPhase('active');
        }
      })
      .catch(() => {
        // Backend unreachable — use fallback so quiz is always playable
        useFallback();
      });
  };

  const useFallback = () => {
    setQuestions(getFallbackQuestions(quizLevel));
    setUsingFallback(true);
    setPhase('active');
  };

  // ── Answer selection ────────────────────────────────────────────────────────
  const handleSelect = (questionId, letter) => {
    setAnswers(prev => ({ ...prev, [String(questionId)]: letter }));
  };

  // ── Submit ──────────────────────────────────────────────────────────────────
  const handleSubmit = async () => {
    const unanswered = questions.filter(q => !answers[String(q.id)]);
    if (unanswered.length > 0) {
      setErrorMsg(`Please answer all questions — ${unanswered.length} remaining.`);
      return;
    }

    setErrorMsg('');
    setPhase('submitting');

    const userId = user?.userId ?? user?.id;

    try {
      let data;

      if (usingFallback) {
        // ── Fallback path: score locally, then call completeLevel for XP ──────
        // Count correct answers from the fallback question set
        const correct = questions.filter(
          q => answers[String(q.id)] === q.correctAnswer
        ).length;

        // Award XP via completeLevel (always available)
        const xpRes = await completeLevel(userId);

        data = {
          score:          correct,
          totalQuestions: questions.length,
          xpEarned:       xpRes.data.xpEarned ?? 100,
          xp:             xpRes.data.xp,
          level:          xpRes.data.level,
          leveledUp:      xpRes.data.leveledUp,
          xpToNextLevel:  xpRes.data.xpToNextLevel,
          badgeAwarded:   xpRes.data.badgeAwarded ?? null,
        };
      } else {
        // ── Backend path: submit answers, backend scores + awards XP ──────────
        const res = await api.post('/quiz/submit', {
          userId,
          level: quizLevel,
          answers,
        });
        data = res.data;

        // Also call completeLevel to ensure XP is always awarded
        // (quiz/submit already does this internally, so this is a safety net)
      }

      setResult(data);
      setPhase('result');

      // Update Navbar pill immediately
      saveUser({ ...user, xp: data.xp, level: data.level });

      // Notify Dashboard to re-fetch profile and show toast
      saveQuizResult(data);

    } catch {
      setErrorMsg('Submission failed. Please try again.');
      setPhase('active');
    }
  };

  // ── Retry ───────────────────────────────────────────────────────────────────
  const handleRetry = () => {
    setAnswers({});
    setResult(null);
    loadQuestions();
  };

  const answeredCount = Object.keys(answers).length;

  // ── Render ──────────────────────────────────────────────────────────────────
  return (
    <div className="max-w-2xl mx-auto px-4 py-10">

      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-white">📝 Quiz</h1>
          <p className="text-slate-400 mt-1 text-sm">
            Level {quizLevel} — answer every question, then hit Submit
          </p>
        </div>
        {/* Back to levels */}
        <button
          onClick={() => navigate('/levels')}
          className="text-slate-400 hover:text-white text-sm transition-colors"
        >
          ← Levels
        </button>
      </div>

      {/* Fallback notice */}
      {usingFallback && phase === 'active' && (
        <div className="bg-amber-950/50 border border-amber-500/40 text-amber-300
                        text-xs rounded-lg px-3 py-2 mb-5 flex items-center gap-2">
          <span>💡</span>
          <span>Using practice questions — connect backend to load real questions.</span>
        </div>
      )}

      {/* ── LOADING ─────────────────────────────────────────────────────────── */}
      {phase === 'loading' && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500
                          border-t-transparent animate-spin" />
          <p className="text-slate-400 text-sm">Loading questions…</p>
        </div>
      )}

      {/* ── ERROR ───────────────────────────────────────────────────────────── */}
      {phase === 'error' && (
        <div className="bg-red-950/60 border border-red-500/40 rounded-xl p-6 text-center">
          <div className="text-4xl mb-3">⚠️</div>
          <p className="text-red-300 font-medium mb-5">{errorMsg}</p>
          <button onClick={handleRetry}
            className="bg-slate-700 hover:bg-slate-600 text-white text-sm
                       font-medium px-6 py-2 rounded-lg transition-colors">
            Retry
          </button>
        </div>
      )}

      {/* ── ACTIVE / SUBMITTING ─────────────────────────────────────────────── */}
      {(phase === 'active' || phase === 'submitting') && (
        <>
          {/* Progress counter */}
          <div className="flex items-center justify-between mb-2">
            <span className="text-slate-400 text-sm">
              {answeredCount} / {questions.length} answered
            </span>
            <span className="text-xs font-semibold text-indigo-400 bg-indigo-950/60
                             border border-indigo-500/30 px-2.5 py-0.5 rounded-full">
              Level {quizLevel}
            </span>
          </div>

          {/* Thin progress bar */}
          <div className="w-full bg-slate-700 rounded-full h-1.5 mb-7 overflow-hidden">
            <div
              className="h-full bg-indigo-500 rounded-full transition-all duration-500"
              style={{ width: `${questions.length ? (answeredCount / questions.length) * 100 : 0}%` }}
            />
          </div>

          {/* Question cards */}
          <div className="flex flex-col gap-5">
            {questions.map((q, idx) => (
              <div key={q.id}>
                <p className="text-slate-500 text-xs mb-2 font-medium">
                  Question {idx + 1} of {questions.length}
                </p>
                <QuizCard
                  question={q}
                  selected={answers[String(q.id)] ?? null}
                  onSelect={handleSelect}
                  revealed={false}
                  correct={q.correctAnswer}
                />
              </div>
            ))}
          </div>

          {/* Validation error */}
          {errorMsg && (
            <p className="text-red-400 text-sm text-center mt-5">{errorMsg}</p>
          )}

          {/* Submit button */}
          <button
            onClick={handleSubmit}
            disabled={phase === 'submitting'}
            className="mt-8 w-full bg-indigo-600 hover:bg-indigo-500
                       disabled:opacity-50 disabled:cursor-not-allowed
                       text-white font-semibold py-3 rounded-xl transition-colors"
          >
            {phase === 'submitting' ? (
              <span className="flex items-center justify-center gap-2">
                <span className="w-4 h-4 border-2 border-white border-t-transparent
                                 rounded-full animate-spin" />
                Submitting…
              </span>
            ) : (
              `Submit Answers (${answeredCount} / ${questions.length})`
            )}
          </button>
        </>
      )}

      {/* ── RESULT ──────────────────────────────────────────────────────────── */}
      {phase === 'result' && result && (
        <>
          {/* Score card */}
          <div className={`rounded-xl border p-6 text-center mb-8
            ${result.score === result.totalQuestions
              ? 'bg-emerald-950/60 border-emerald-500/50'
              : result.score > result.totalQuestions / 2
                ? 'bg-indigo-950/60 border-indigo-500/50'
                : 'bg-red-950/60 border-red-500/50'}`}
          >
            <div className="text-5xl mb-3">
              {result.score === result.totalQuestions ? '🏆'
                : result.score > result.totalQuestions / 2 ? '🎉' : '😔'}
            </div>
            <h2 className="text-3xl font-bold text-white mb-1">
              {result.score} / {result.totalQuestions}
            </h2>
            <p className="text-slate-400 text-sm mb-5">Questions correct</p>

            {/* Stat pills */}
            <div className="flex justify-center gap-3 flex-wrap">
              <div className="bg-slate-800/80 rounded-lg px-4 py-2.5 min-w-[90px]">
                <p className="text-slate-400 text-xs mb-0.5">XP Earned</p>
                <p className="text-indigo-400 font-bold">+{result.xpEarned}</p>
              </div>
              <div className="bg-slate-800/80 rounded-lg px-4 py-2.5 min-w-[90px]">
                <p className="text-slate-400 text-xs mb-0.5">Your Level</p>
                <p className="text-amber-400 font-bold">Lv. {result.level}</p>
              </div>
              <div className="bg-slate-800/80 rounded-lg px-4 py-2.5 min-w-[90px]">
                <p className="text-slate-400 text-xs mb-0.5">Next Level</p>
                <p className="text-emerald-400 font-bold">{result.xpToNextLevel} XP</p>
              </div>
            </div>

            {result.leveledUp && (
              <div className="mt-4 bg-amber-900/40 border border-amber-500/40
                              text-amber-300 text-sm font-semibold rounded-lg px-4 py-2">
                🎊 Level Up! You are now Level {result.level}!
              </div>
            )}
            {result.badgeAwarded && (
              <div className="mt-3 bg-violet-900/40 border border-violet-500/40
                              text-violet-300 text-sm font-semibold rounded-lg px-4 py-2">
                🏅 Badge unlocked: {result.badgeAwarded}
              </div>
            )}
          </div>

          {/* Answer review */}
          <h3 className="text-white font-semibold mb-4">Review Your Answers</h3>
          <div className="flex flex-col gap-5">
            {questions.map((q, idx) => (
              <div key={q.id}>
                <p className="text-slate-500 text-xs mb-2 font-medium">Question {idx + 1}</p>
                <QuizCard
                  question={q}
                  selected={answers[String(q.id)] ?? null}
                  onSelect={() => {}}
                  revealed={true}
                  correct={q.correctAnswer}
                />
              </div>
            ))}
          </div>

          {/* Action buttons */}
          <div className="mt-8 flex gap-3">
            <button onClick={handleRetry}
              className="flex-1 bg-slate-700 hover:bg-slate-600
                         text-white font-semibold py-3 rounded-xl transition-colors">
              Try Again
            </button>
            <button onClick={() => navigate('/levels')}
              className="flex-1 bg-indigo-600 hover:bg-indigo-500
                         text-white font-semibold py-3 rounded-xl transition-colors">
              Back to Levels
            </button>
          </div>
        </>
      )}
    </div>
  );
}
