// Quiz.jsx — Premium Game-Feel Quiz (Learning Path 2.0)
// Questions are served WITHOUT answers (secure), and the backend decides pass/fail.
import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth }   from '../context/AuthContext';
import { getLevelQuiz, submitLevelQuiz, getQuizQuestions, submitQuiz } from '../api/endpoints';
import CountUp       from '../components/CountUp';
import ConfettiAnimation from '../components/ConfettiAnimation';
import { SkeletonQuiz } from '../components/SkeletonCard';

// Lightweight fallback used ONLY if the backend is unreachable.
// Contains no answer keys — submission requires the backend.
const OPTIONS = ['A', 'B', 'C', 'D'];

export default function Quiz() {
  const { user, saveUser, saveQuizResult } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const quizLevel = parseInt(searchParams.get('level') ?? '1', 10);

const [questions,     setQuestions]     = useState([]);
  const [answers,       setAnswers]       = useState({});
  const [result,        setResult]        = useState(null);
  const [phase,         setPhase]         = useState('loading');
  const [errorMsg,      setErrorMsg]      = useState('');
  const [usingFallback, setUsingFallback] = useState(false);
  const [showConfetti,  setShowConfetti]  = useState(false);

  const applyFallback = () => {
    const base = quizLevel * 7;
    setQuestions([
      { id: `f${base + 1}`, question: 'Practice question (backend offline). Please start the backend to take a real quiz.', optionA: 'Financial literacy', optionB: 'Irrelevant', optionC: 'Invalid', optionD: 'N/A' },
    ]);
    setUsingFallback(true);
    setPhase('active');
  };

  const loadQuestions = () => {
    setPhase('loading'); setErrorMsg(''); setAnswers({}); setResult(null); setUsingFallback(false);
    getLevelQuiz(quizLevel)
      .then(({ data }) => {
        if (!data || data.length === 0) {
          // Try the legacy secure quiz endpoint as a fallback
          getQuizQuestions(quizLevel)
            .then(({ data: legacy }) => {
              if (!legacy || legacy.length === 0) { applyFallback(); }
              else { setQuestions(legacy); setPhase('active'); }
            })
            .catch(() => applyFallback());
        } else {
          setQuestions(data); setPhase('active');
        }
      })
      .catch(() => applyFallback());
  };

  useEffect(() => { loadQuestions(); }, [quizLevel]);

  const handleSelect = (questionId, letter) => {
    if (phase !== 'active') return;
    setAnswers(prev => ({ ...prev, [String(questionId)]: letter }));
  };

  const handleSubmit = async () => {
    const unanswered = questions.filter(q => !answers[String(q.id)]);
    if (unanswered.length > 0) { setErrorMsg(`Please answer all questions — ${unanswered.length} remaining.`); return; }
    setErrorMsg(''); setPhase('submitting');
    try {
      let data;
      if (usingFallback) {
        // No backend — do a purely local practice. No rewards, no unlock.
        data = {
          score: 0, totalQuestions: questions.length, passed: false,
          xpEarned: 0, coinsEarned: 0, level: user?.level ?? 1, currentXp: user?.xp ?? 0,
          badgesAwarded: [], achievementsUnlocked: [],
        };
      } else {
        const answersMap = {};
        questions.forEach(q => { answersMap[String(q.id)] = answers[String(q.id)]; });
        try {
          const res = await submitLevelQuiz(quizLevel, answersMap);
          data = res.data;
        } catch {
          // Fall back to legacy secure submit
          const legacy = await submitQuiz({ level: quizLevel, answers: answersMap });
          data = legacy.data;
        }
      }
      setResult(data); setPhase('result');

      const passed = data.passed ?? ((data.score ?? 0) * 100 >= (data.totalQuestions ?? 1) * 70);
      const newBadges  = data.badgesAwarded ?? [];

      if (passed && !usingFallback) setShowConfetti(true);

      if (newBadges.length > 0) {
        const msg = `Amazing! You unlocked the "${newBadges[0].badgeName}" badge! 🏅`;
      } else if (data.leveledUp) {
        const msg = `Incredible! You reached Level ${data.level}! Keep going! 🚀`;
      } else if (passed && !usingFallback) {
        const msg = `Great job! You scored ${data.score} out of ${data.totalQuestions}! 🎉`;
      } else if (!usingFallback) {
        const msg = `Don't give up! Review the material and try again. You've got this! 💪`;
      }

      const latestXp = data.currentXp ?? data.xp ?? user?.xp ?? 0;
      const latestLevel = data.level ?? (passed ? Math.max(user?.level ?? 1, quizLevel + 1) : user?.level ?? 1);

      if (!usingFallback) {
        saveUser({ ...(user ?? {}), xp: latestXp, level: latestLevel });
      }
      saveQuizResult(data);
    } catch {
      setErrorMsg('Submission failed. Please try again.');
      setPhase('active');
    }
  };

  const handleRetry = () => { setAnswers({}); setResult(null); setShowConfetti(false); loadQuestions(); };

  const answeredCount = Object.keys(answers).length;
  const progressPct   = questions.length ? (answeredCount / questions.length) * 100 : 0;
  const quizPassed    = result ? (result.passed ?? ((result.score ?? 0) * 100 >= (result.totalQuestions ?? 1) * 70)) : false;

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">

      {showConfetti && <ConfettiAnimation />}

      {/* Header */}
      <div className="mb-6 flex items-center justify-between fade-slide-up">
        <div>
          <h1 className="text-2xl font-bold text-white">📝 Quiz</h1>
          <p className="text-slate-400 mt-0.5 text-sm">Level {quizLevel}</p>
        </div>
        <button
          onClick={() => navigate('/levels')}
          className="text-slate-400 hover:text-white text-sm transition-colors flex items-center gap-1 px-3 py-1.5 rounded-lg hover:bg-slate-800"
        >
          ← Levels
        </button>
      </div>

      {usingFallback && phase === 'active' && (
        <div className="bg-amber-950/50 border border-amber-500/40 text-amber-300 text-xs rounded-lg px-3 py-2 mb-5 flex items-center gap-2">
          <span>💡</span><span>Practice mode — start the backend to take the real quiz and earn rewards.</span>
        </div>
      )}

      {/* LOADING */}
      {phase === 'loading' && <SkeletonQuiz />}

      {/* ACTIVE / SUBMITTING */}
      {(phase === 'active' || phase === 'submitting') && (
        <>
          {/* Progress header */}
          <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-4 mb-6 fade-slide-up">
            <div className="flex items-center justify-between mb-2">
              <span className="text-slate-300 text-sm font-semibold">
                {answeredCount} <span className="text-slate-500">/ {questions.length}</span> answered
              </span>
              <span className="text-xs font-bold text-indigo-400 bg-indigo-950/60 border border-indigo-500/30 px-2.5 py-0.5 rounded-full">
                Level {quizLevel}
              </span>
            </div>
            {/* Progress bar */}
            <div className="w-full bg-slate-700/80 rounded-full h-2 overflow-hidden mb-3">
              <div
                className="h-full rounded-full transition-all duration-500"
                style={{ width: `${progressPct}%`, background: 'linear-gradient(90deg, #6366f1, #8b5cf6)' }}
              />
            </div>
            {/* Question dots */}
            <div className="flex gap-1.5 flex-wrap">
              {questions.map((q, i) => {
                const answered = !!answers[String(q.id)];
                return (
                  <div
                    key={q.id}
                    title={`Question ${i + 1}`}
                    className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold transition-all duration-200
                      ${answered ? 'bg-indigo-600 text-white scale-110' : 'bg-slate-700 text-slate-500'}`}
                  >
                    {answered ? '✓' : i + 1}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Question cards */}
          <div className="flex flex-col gap-5">
            {questions.map((q, idx) => (
              <div key={q.id} className="fade-slide-up" style={{ animationDelay: `${idx * 40}ms` }}>
                <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5">
                  <div className="flex items-center gap-2 mb-3">
                    <span className="text-xs font-bold text-slate-500 bg-slate-700/60 px-2 py-0.5 rounded-full">
                      Q{idx + 1}
                    </span>
                    {answers[String(q.id)] && (
                      <span className="text-xs text-indigo-400 font-semibold">✓ Answered</span>
                    )}
                  </div>
                  <p className="text-white font-semibold mb-4 leading-relaxed">{q.question}</p>
                  <div className="flex flex-col gap-2">
                    {OPTIONS.map(letter => {
                      const isSelected = answers[String(q.id)] === letter;
                      const optionText = q[`option${letter}`] ?? (q.options ? q.options[letter.charCodeAt(0) - 65] : '');
                      return (
                        <button
                          key={letter}
                          disabled={phase === 'submitting'}
                          onClick={() => handleSelect(q.id, letter)}
                          className={`quiz-option w-full text-left px-4 py-3 rounded-xl border text-sm font-medium
                            ${isSelected
                              ? 'bg-indigo-600/30 border-indigo-400/70 text-white quiz-option-selected'
                              : 'bg-slate-700/40 border-slate-600/60 text-slate-300 hover:border-indigo-500/50 hover:text-white hover:bg-slate-700/60'
                            }`}
                        >
                          <span className={`inline-flex w-6 h-6 rounded-full items-center justify-center text-xs font-bold mr-3 flex-shrink-0
                            ${isSelected ? 'bg-indigo-500 text-white' : 'bg-slate-600 text-slate-400'}`}>
                            {letter}
                          </span>
                          {optionText}
                        </button>
                      );
                    })}
                  </div>
                </div>
              </div>
            ))}
          </div>

          {errorMsg && (
            <p className="text-red-400 text-sm text-center mt-5 bg-red-950/40 border border-red-500/30 rounded-lg px-4 py-2">
              {errorMsg}
            </p>
          )}

          <button
            onClick={handleSubmit}
            disabled={phase === 'submitting'}
            className="btn-primary mt-8 w-full py-3.5 rounded-xl text-base"
          >
            {phase === 'submitting' ? (
              <span className="flex items-center justify-center gap-2">
                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                Submitting…
              </span>
            ) : `Submit Answers (${answeredCount} / ${questions.length})`}
          </button>
        </>
      )}

      {/* RESULT */}
      {phase === 'result' && result && (
        <div style={{ animation: 'fadeSlideUp 0.4s ease' }}>
          {/* Score card */}
          <div className={`rounded-2xl border p-7 text-center mb-6 relative overflow-hidden
            ${quizPassed ? 'border-emerald-500/50' : 'border-red-500/40'}`}
            style={{
              background: quizPassed
                ? 'linear-gradient(135deg, rgba(16,185,129,0.1), rgba(5,150,105,0.06))'
                : 'linear-gradient(135deg, rgba(239,68,68,0.08), rgba(185,28,28,0.05))',
            }}
          >
            <div className="text-6xl mb-3" style={{ animation: 'bounceIn 0.5s ease' }}>
              {quizPassed ? (result.score === result.totalQuestions ? '🏆' : '🎉') : '😔'}
            </div>
            <h2 className="text-4xl font-black text-white mb-1">
              {result.score} / {result.totalQuestions}
            </h2>
            <p className={`text-lg font-semibold mb-1 ${quizPassed ? 'text-emerald-400' : 'text-red-400'}`}>
              {quizPassed ? 'Excellent!' : `Nice try, ${user?.name?.split(' ')[0] ?? 'there'}!`}
            </p>
            <p className="text-slate-400 text-sm mb-5">
              {quizPassed
                ? 'Great work! Keep up the momentum.'
                : "You're close. Review the material and try again."}
            </p>

            {/* Stat pills */}
            <div className="flex justify-center gap-3 flex-wrap">
              <div className="bg-slate-800/80 rounded-xl px-4 py-3 min-w-[90px]">
                <p className="text-slate-400 text-xs mb-1">XP Earned</p>
                <CountUp from={0} to={result.xpEarned ?? 0} duration={800} className="text-indigo-400 font-black text-lg" suffix=" XP" />
              </div>
              {(result.coinsEarned ?? 0) > 0 && (
                <div className="bg-slate-800/80 rounded-xl px-4 py-3 min-w-[90px]">
                  <p className="text-slate-400 text-xs mb-1">Coins</p>
                  <CountUp from={0} to={result.coinsEarned} duration={700} className="text-yellow-400 font-black text-lg" suffix=" 🪙" />
                </div>
              )}
              <div className="bg-slate-800/80 rounded-xl px-4 py-3 min-w-[90px]">
                <p className="text-slate-400 text-xs mb-1">Level</p>
                <p className="text-amber-400 font-black text-lg">Lv. {result.level}</p>
              </div>
              {result.xpToNextLevel > 0 && (
                <div className="bg-slate-800/80 rounded-xl px-4 py-3 min-w-[90px]">
                  <p className="text-slate-400 text-xs mb-1">Next Level</p>
                  <p className="text-emerald-400 font-black text-lg">{result.xpToNextLevel} XP</p>
                </div>
              )}
            </div>

            {result.leveledUp && (
              <div className="mt-4 bg-amber-900/40 border border-amber-500/40 text-amber-300 text-sm font-bold rounded-xl px-4 py-2.5"
                style={{ animation: 'badge-pop 0.5s ease' }}>
                🎊 Level Up! You are now Level {result.level}!
              </div>
            )}
            {(result.badgesAwarded?.length > 0) && result.badgesAwarded.map(b => (
              <div key={b.badgeCode} className="mt-3 bg-violet-900/40 border border-violet-500/40 text-violet-300 text-sm font-bold rounded-xl px-4 py-2.5"
                style={{ animation: 'badge-pop 0.5s 0.1s ease both' }}>
                {b.icon ?? '🏅'} Badge unlocked: {b.badgeName}
              </div>
            ))}
            {result.achievementsUnlocked?.length > 0 && result.achievementsUnlocked.map(a => (
              <div key={a.code} className="mt-2 bg-emerald-900/40 border border-emerald-500/40 text-emerald-300 text-sm font-bold rounded-xl px-4 py-2.5">
                {a.icon ?? '🏆'} Achievement: {a.name}
              </div>
            ))}
            {result.nextLevelUnlocked && (
              <div className="mt-3 bg-indigo-900/40 border border-indigo-500/40 text-indigo-300 text-sm font-bold rounded-xl px-4 py-2.5">
                🔓 Next level unlocked!
              </div>
            )}
          </div>

          {/* Action buttons */}
          <div className="mt-8 flex gap-3">
            {!quizPassed && (
              <button onClick={handleRetry} className="btn-secondary flex-1 py-3 rounded-xl">
                🔄 Try Again
              </button>
            )}
            <button onClick={() => navigate('/levels')} className="btn-primary flex-1 py-3 rounded-xl">
              🗺️ Back to Levels
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
