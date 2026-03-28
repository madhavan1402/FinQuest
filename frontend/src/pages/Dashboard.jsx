// src/pages/Dashboard.jsx
// Fetches user profile + achievements on mount.
// Re-fetches profile whenever a quiz is submitted (via lastQuizResult in AuthContext).
// Shows a toast notification after quiz submission.

import { useState, useEffect, useRef } from 'react';
import { useAuth }        from '../context/AuthContext';
import { getUserProfile, getAchievements } from '../api/endpoints';
import StatCard    from '../components/StatCard';
import ProgressBar from '../components/ProgressBar';

// XP needed to reach the next level = currentLevel × 100
const xpThreshold = (level) => level * 100;

export default function Dashboard() {
  const { user, saveUser, lastQuizResult } = useAuth();

  const [profile,      setProfile]      = useState(null);
  const [achievements, setAchievements] = useState([]);
  const [loading,      setLoading]      = useState(true);
  const [error,        setError]        = useState('');
  const [toast,        setToast]        = useState(null);
  const toastTimer = useRef(null);

  // ── Fetch profile from backend ──────────────────────────────────────────────
  // Wrapped in useCallback pattern via userId dep to avoid stale closure
  const fetchProfile = (userId) => {
    setLoading(true);
    getUserProfile(userId)
      .then(({ data }) => {
        setProfile(data);
        setError('');
        // Sync fresh values into AuthContext so Navbar pill stays current
        saveUser({ ...user, xp: data.xp, level: data.level, financialScore: data.financialScore });
      })
      .catch(() => setError('Could not load profile. Is the backend running on port 8080?'))
      .finally(() => setLoading(false));
  };

  // ── Fetch achievements ──────────────────────────────────────────────────────
  const fetchAchievements = (userId) => {
    getAchievements(userId)
      .then(({ data }) => setAchievements(data))
      .catch(() => {}); // achievements are non-critical — fail silently
  };

  // ── Initial load ────────────────────────────────────────────────────────────
  useEffect(() => {
    const uid = user?.userId;
    if (!uid) {
      setError('No user session found. Please log in again.');
      setLoading(false);
      return;
    }
    fetchProfile(uid);
    fetchAchievements(uid);
  }, []); // runs once on mount

  // ── Re-fetch after quiz submission ──────────────────────────────────────────
  // lastQuizResult changes every time Quiz.jsx calls saveQuizResult()
  useEffect(() => {
    if (!lastQuizResult || !user?.userId) return;
    fetchProfile(user.userId);   // pull fresh XP + level from DB
    showToast(lastQuizResult);   // display the result notification
  }, [lastQuizResult]);

  // ── Toast helpers ───────────────────────────────────────────────────────────
  const showToast = (result) => {
    if (toastTimer.current) clearTimeout(toastTimer.current);
    setToast(result);
    toastTimer.current = setTimeout(() => setToast(null), 4000);
  };

  useEffect(() => () => {
    if (toastTimer.current) clearTimeout(toastTimer.current);
  }, []);

  // ── Derived values ──────────────────────────────────────────────────────────
  const xpMax       = profile ? xpThreshold(profile.level) : 100;
  const xpRemaining = profile ? Math.max(xpMax - profile.xp, 0) : 0;

  // ── Render ──────────────────────────────────────────────────────────────────
  return (
    <div className="max-w-6xl mx-auto px-4 py-10">

      {/* ── Toast notification ───────────────────────────────────────────────── */}
      {toast && (
        <div className="fixed top-20 left-1/2 -translate-x-1/2 z-50 w-full max-w-md px-4">
          <div className={`rounded-xl border px-5 py-4 shadow-2xl
            ${toast.leveledUp
              ? 'bg-amber-900/95 border-amber-400/60'
              : 'bg-emerald-900/95 border-emerald-400/60'}`}
          >
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-2">
                <span className="text-2xl">{toast.leveledUp ? '🎊' : '✅'}</span>
                <span className="text-white font-bold text-sm">
                  {toast.leveledUp
                    ? `Level Up! You reached Level ${toast.level}!`
                    : 'Quiz submitted successfully!'}
                </span>
              </div>
              <button onClick={() => setToast(null)}
                className="text-slate-400 hover:text-white text-xl leading-none ml-3">×</button>
            </div>
            <div className="flex gap-4 text-xs flex-wrap">
              <span className="text-slate-300">
                Score: <strong className="text-white">{toast.score}/{toast.totalQuestions}</strong>
              </span>
              <span className="text-slate-300">
                XP: <strong className="text-indigo-300">+{toast.xpEarned}</strong>
              </span>
              <span className="text-slate-300">
                Level: <strong className="text-amber-300">{toast.level}</strong>
              </span>
            </div>
            {toast.badgeAwarded && (
              <p className="mt-2 text-xs text-violet-300 font-medium">
                🏅 Badge unlocked: {toast.badgeAwarded}
              </p>
            )}
          </div>
        </div>
      )}

      {/* ── Page header ──────────────────────────────────────────────────────── */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">
          Welcome back,{' '}
          <span className="text-indigo-400">{profile?.name ?? user?.name ?? '…'}</span> 👋
        </h1>
        <p className="text-slate-400 mt-1">Here's your financial progress overview</p>
      </div>

      {/* ── Loading ───────────────────────────────────────────────────────────── */}
      {loading && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500
                          border-t-transparent animate-spin" />
          <p className="text-slate-400 text-sm">Loading your profile…</p>
        </div>
      )}

      {/* ── Error ─────────────────────────────────────────────────────────────── */}
      {!loading && error && (
        <div className="bg-red-950/60 border border-red-500/40 rounded-xl p-6 text-center">
          <div className="text-4xl mb-3">⚠️</div>
          <p className="text-red-300 font-medium">{error}</p>
          <button
            onClick={() => fetchProfile(user?.userId)}
            className="mt-4 bg-slate-700 hover:bg-slate-600 text-white text-sm
                       px-5 py-2 rounded-lg transition-colors"
          >
            Retry
          </button>
        </div>
      )}

      {/* ── Main content ─────────────────────────────────────────────────────── */}
      {!loading && !error && profile && (
        <>
          {/* Stat cards — XP, Level, Financial Score */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
            <StatCard icon="🎯" label="Total XP"
              value={`${profile.xp} XP`}
              sub={`${xpRemaining} XP to next level`}
              accent="text-indigo-400" />
            <StatCard icon="⚡" label="Current Level"
              value={`Level ${profile.level}`}
              sub="Keep completing quizzes!"
              accent="text-amber-400" />
            <StatCard icon="💹" label="Financial Score"
              value={profile.financialScore}
              sub="Out of 100"
              accent="text-emerald-400" />
          </div>

          {/* XP Progress bar */}
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-5 mb-8">
            <div className="flex items-center justify-between mb-3">
              <h2 className="text-white font-semibold">XP Progress</h2>
              <span className="text-slate-400 text-sm">
                {xpRemaining} XP to Level {profile.level + 1}
              </span>
            </div>
            <ProgressBar value={profile.xp} max={xpMax} label={`Level ${profile.level}`} />
          </div>

          {/* Achievements + Chart row */}
          <div className="grid md:grid-cols-2 gap-6">

            {/* Achievements — real data from GET /api/achievements */}
            <div className="bg-slate-800 border border-slate-700 rounded-xl p-5">
              <h2 className="text-white font-semibold mb-4">🏅 Achievements</h2>
              {achievements.length === 0 ? (
                <p className="text-slate-500 text-sm">
                  Complete quizzes and simulations to earn badges!
                </p>
              ) : (
                <div className="flex flex-wrap gap-2">
                  {achievements.map((a) => (
                    <span key={a.id}
                      className="bg-indigo-900/60 border border-indigo-500/40
                                 text-indigo-300 text-xs px-3 py-1.5 rounded-full">
                      🏅 {a.badgeName}
                    </span>
                  ))}
                </div>
              )}
            </div>

            {/* Chart placeholder */}
            <div className="bg-slate-800 border border-slate-700 rounded-xl p-5">
              <h2 className="text-white font-semibold mb-4">📈 Investment Growth</h2>
              <div className="w-full h-40 bg-slate-700/50 rounded-lg border border-slate-600
                              flex items-center justify-center">
                <div className="text-center">
                  <div className="text-3xl mb-2">📊</div>
                  <p className="text-slate-400 text-sm">Chart coming soon</p>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
