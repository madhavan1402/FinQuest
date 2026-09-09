// src/pages/Assessment.jsx — Intelligent Financial Onboarding Questionnaire & Results
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useMentor } from '../context/MentorContext';
import { getAssessmentQuestions, submitAssessment } from '../api/endpoints';
import CountUp from '../components/CountUp';


export default function Assessment() {
  const { user, saveUser } = useAuth();
  const mentor             = useMentor();
  const navigate           = useNavigate();

  const [questions, setQuestions]     = useState([]);
  const [answers, setAnswers]         = useState({});
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading]         = useState(true);
  const [submitting, setSubmitting]   = useState(false);
  const [error, setError]             = useState('');
  const [result, setResult]           = useState(null);

  // Fetch assessment questions on mount
  useEffect(() => {
    let isMounted = true;
    setLoading(true);
    if (mentor) {
      mentor.encourage('Welcome to the Financial Assessment! Answer honestly so I can personalize your learning path.');
    }
    getAssessmentQuestions()
      .then((res) => {
        if (!isMounted) return;
        const qList = res.data?.data || res.data || [];
        setQuestions(qList);
        setError('');
      })
      .catch((err) => {
        if (!isMounted) return;
        setError(err.response?.data?.message || 'Failed to load assessment questions. Please try again.');
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, []);


  const currentQ = questions[currentIndex];
  const totalQ   = questions.length;
  const answeredCount = Object.keys(answers).length;
  const progressPercent = totalQ > 0 ? Math.round((answeredCount / totalQ) * 100) : 0;

  const handleSelectOption = (questionId, optionKey) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: optionKey,
    }));
  };

  const handleNext = () => {
    if (currentIndex < totalQ - 1) {
      setCurrentIndex(prev => prev + 1);
    }
  };

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex(prev => prev - 1);
    }
  };

  const handleSubmit = async () => {
    if (answeredCount < totalQ) {
      setError(`Please answer all ${totalQ} questions before submitting.`);
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const payload = { answers };
      const res = await submitAssessment(payload);
      const resData = res.data?.data || res.data;
      setResult(resData);

      if (mentor) {
        mentor.explain(`Assessment complete! You've been placed at ${resData.literacyLevel} with a ${resData.riskProfile} risk profile.`);
      }

      // Update AuthContext and LocalStorage with newly unlocked profile fields
      saveUser({
        ...user,
        financialScore: resData.financialScore,
        literacyLevel: resData.literacyLevel,
        riskProfile: resData.riskProfile,
        recommendation: resData.recommendation,
        assessmentCompleted: true,
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit assessment. Please try again.');
    } finally {
      setSubmitting(false);
    }

  };

  if (loading) {
    return (
      <div className="min-h-[70vh] flex flex-col items-center justify-center p-4">
        <div className="w-12 h-12 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin mb-4" />
        <p className="text-slate-400 font-medium text-sm">Preparing your financial assessment...</p>
      </div>
    );
  }

  // Result View after successful submission
  if (result) {
    const levelBadgeColor =
      result.literacyLevel === 'ADVANCED'
        ? 'bg-emerald-900/50 text-emerald-300 border-emerald-500/50 shadow-emerald-500/20'
        : result.literacyLevel === 'INTERMEDIATE'
        ? 'bg-amber-900/50 text-amber-300 border-amber-500/50 shadow-amber-500/20'
        : 'bg-indigo-900/50 text-indigo-300 border-indigo-500/50 shadow-indigo-500/20';

    return (
      <div className="max-w-3xl mx-auto px-4 py-10" style={{ animation: 'fadeSlideUp 0.4s ease' }}>
        <div className="glow-card bg-slate-800/95 border border-slate-700/80 rounded-2xl p-8 shadow-2xl">
          {/* Header */}
          <div className="text-center mb-8">
            <div className="text-6xl mb-3" style={{ animation: 'bounceIn 0.6s ease' }}>🎉</div>
            <h1 className="text-3xl font-black text-white tracking-tight">Assessment Completed!</h1>
            <p className="text-slate-400 mt-1">Here is your personalized financial baseline</p>
          </div>

          {/* Score & Profile Summary Cards */}
          <div className="grid sm:grid-cols-3 gap-4 mb-8">
            {/* Financial Health Score */}
            <div className="bg-slate-900/90 border border-slate-700/70 rounded-xl p-5 text-center flex flex-col justify-center">
              <span className="text-slate-400 text-xs font-semibold uppercase tracking-wider mb-1">Financial Score</span>
              <div className="text-4xl font-black text-emerald-400 my-1">
                <CountUp from={0} to={result.financialScore} duration={1200} />
                <span className="text-slate-500 text-lg font-normal"> / 100</span>
              </div>
              <span className="text-slate-500 text-xs">Deterministic Baseline</span>
            </div>

            {/* Literacy Level */}
            <div className="bg-slate-900/90 border border-slate-700/70 rounded-xl p-5 text-center flex flex-col justify-center">
              <span className="text-slate-400 text-xs font-semibold uppercase tracking-wider mb-1">Literacy Level</span>
              <div className="my-2">
                <span className={`inline-block px-3.5 py-1 text-sm font-black rounded-full border shadow-sm ${levelBadgeColor}`}>
                  {result.literacyLevel}
                </span>
              </div>
              <span className="text-slate-500 text-xs">{result.recommendedStartingPoint}</span>
            </div>

            {/* Risk Profile */}
            <div className="bg-slate-900/90 border border-slate-700/70 rounded-xl p-5 text-center flex flex-col justify-center">
              <span className="text-slate-400 text-xs font-semibold uppercase tracking-wider mb-1">Risk Profile</span>
              <div className="text-xl font-bold text-violet-300 my-2 flex items-center justify-center gap-1.5">
                <span>🛡️</span>
                <span>{result.riskProfile}</span>
              </div>
              <span className="text-slate-500 text-xs">Independent Risk Tolerance</span>
            </div>
          </div>

          {/* Detailed Summary */}
          <div className="bg-slate-900/60 border border-slate-700/60 rounded-xl p-5 mb-6">
            <h3 className="text-white font-bold text-sm mb-2 flex items-center gap-2">
              <span>📋</span> Diagnostic Summary
            </h3>
            <p className="text-slate-300 text-sm leading-relaxed mb-4">
              {result.summary}
            </p>
            <div className="bg-indigo-950/40 border border-indigo-500/30 rounded-lg p-3.5 flex items-start gap-3">
              <span className="text-xl">💡</span>
              <div>
                <p className="text-indigo-300 text-xs font-bold uppercase tracking-wider mb-0.5">Recommended Next Action</p>
                <p className="text-slate-300 text-xs">{result.recommendation}</p>
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <button
              onClick={() => navigate('/dashboard')}
              className="btn-primary py-3 px-8 rounded-xl font-bold text-sm flex items-center justify-center gap-2"
            >
              <span>Go to Dashboard</span>
              <span>→</span>
            </button>
            <button
              onClick={() => navigate('/learning-path')}
              className="btn-secondary py-3 px-8 rounded-xl font-bold text-sm flex items-center justify-center gap-2"
            >
              <span>Explore Learning Path</span>
              <span>🧭</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Questionnaire Flow
  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      {/* Title & Progress */}
      <div className="mb-6 fade-slide-up">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-3">
          <div>
            <h1 className="text-2xl sm:text-3xl font-black text-white tracking-tight flex items-center gap-2">
              <span>🧭</span> Financial Assessment
            </h1>
            <p className="text-slate-400 text-xs sm:text-sm mt-0.5">
              10 quick questions to establish your baseline literacy and calibrate your path
            </p>
          </div>
          <div className="text-right">
            <span className="text-indigo-400 font-bold text-sm">
              {answeredCount} of {totalQ} Answered
            </span>
            <p className="text-slate-500 text-xs">{progressPercent}% complete</p>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="w-full bg-slate-800 rounded-full h-2.5 overflow-hidden border border-slate-700">
          <div
            className="h-full rounded-full transition-all duration-300"
            style={{
              width: `${progressPercent}%`,
              background: 'linear-gradient(90deg, #6366f1, #8b5cf6, #10b981)',
            }}
          />
        </div>
      </div>

      {error && (
        <div className="bg-red-900/40 border border-red-500/50 text-red-300 rounded-xl px-4 py-3 text-sm mb-6 flex items-center gap-2">
          <span>⚠️</span>
          <span>{error}</span>
        </div>
      )}

      {/* Question Card */}
      {currentQ ? (
        <div className="glow-card bg-slate-800/95 border border-slate-700/80 rounded-2xl p-6 sm:p-8 mb-6 shadow-xl">
          {/* Dimension Tag */}
          <div className="flex items-center justify-between gap-2 mb-4">
            <span className="bg-indigo-950/80 border border-indigo-500/40 text-indigo-300 text-xs px-3 py-1 rounded-full font-semibold uppercase tracking-wider">
              {currentQ.category}
            </span>
            <span className="text-slate-500 text-xs font-medium">
              Question {currentIndex + 1} of {totalQ}
            </span>
          </div>

          {/* Question Text */}
          <h2 className="text-lg sm:text-xl font-bold text-white mb-6 leading-snug">
            {currentQ.questionText}
          </h2>

          {/* Options */}
          <div className="space-y-3 mb-6">
            {currentQ.options?.map((opt) => {
              const isSelected = answers[currentQ.id] === opt.key;
              return (
                <button
                  key={opt.key}
                  type="button"
                  onClick={() => handleSelectOption(currentQ.id, opt.key)}
                  className={`w-full text-left p-4 rounded-xl border transition-all duration-200 flex items-start gap-3.5 ${
                    isSelected
                      ? 'bg-indigo-900/40 border-indigo-500 text-white shadow-lg shadow-indigo-500/10 ring-1 ring-indigo-500'
                      : 'bg-slate-900/60 border-slate-700/70 text-slate-300 hover:bg-slate-700/40 hover:border-slate-600'
                  }`}
                >
                  <span
                    className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-black flex-shrink-0 mt-0.5 border ${
                      isSelected
                        ? 'bg-indigo-600 border-indigo-400 text-white'
                        : 'bg-slate-800 border-slate-600 text-slate-400'
                    }`}
                  >
                    {opt.key}
                  </span>
                  <span className="text-sm font-medium leading-relaxed">
                    {opt.text}
                  </span>
                </button>
              );
            })}
          </div>

          {/* Navigation Controls */}
          <div className="flex items-center justify-between pt-4 border-t border-slate-700/60">
            <button
              onClick={handlePrev}
              disabled={currentIndex === 0}
              className="btn-secondary px-5 py-2.5 rounded-xl text-sm font-bold disabled:opacity-30"
            >
              ← Previous
            </button>

            {currentIndex < totalQ - 1 ? (
              <button
                onClick={handleNext}
                className="btn-primary px-6 py-2.5 rounded-xl text-sm font-bold flex items-center gap-2"
              >
                <span>Next</span>
                <span>→</span>
              </button>
            ) : (
              <button
                onClick={handleSubmit}
                disabled={submitting || answeredCount < totalQ}
                className="btn-primary px-7 py-2.5 rounded-xl text-sm font-bold flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-40"
              >
                {submitting ? (
                  <>
                    <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    <span>Calculating Score…</span>
                  </>
                ) : (
                  <>
                    <span>Submit Assessment</span>
                    <span>✓</span>
                  </>
                )}
              </button>
            )}
          </div>
        </div>
      ) : (
        <div className="bg-slate-800 border border-slate-700 rounded-2xl p-8 text-center text-slate-400">
          No assessment questions found.
        </div>
      )}

      {/* Question Navigation Dots */}
      <div className="flex items-center justify-center gap-2 flex-wrap">
        {questions.map((q, idx) => {
          const isAnswered = !!answers[q.id];
          const isCurrent = idx === currentIndex;
          return (
            <button
              key={q.id}
              onClick={() => setCurrentIndex(idx)}
              className={`w-8 h-8 rounded-lg text-xs font-bold transition-all ${
                isCurrent
                  ? 'bg-indigo-600 text-white ring-2 ring-indigo-400'
                  : isAnswered
                  ? 'bg-emerald-950/70 text-emerald-400 border border-emerald-500/40'
                  : 'bg-slate-800 text-slate-400 border border-slate-700 hover:bg-slate-700'
              }`}
              title={`Question ${idx + 1} (${isAnswered ? 'Answered' : 'Unanswered'})`}
            >
              {idx + 1}
            </button>
          );
        })}
      </div>
    </div>
  );
}
