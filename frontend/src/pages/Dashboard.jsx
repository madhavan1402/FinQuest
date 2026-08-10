// src/pages/Dashboard.jsx — Premium FinQuest Dashboard
import { useState, useEffect, useRef } from 'react';
import { useAuth }        from '../context/AuthContext';
import { getUserProfile, getGamificationSummary } from '../api/endpoints';
import CountUp            from '../components/CountUp';
import Modal              from '../components/Modal';
import Toast              from '../components/Toast';
import { SkeletonStatCard, SkeletonCard } from '../components/SkeletonCard';

let toastIdCounter = 0;

export default function Dashboard() {
  const { user, saveUser, lastQuizResult } = useAuth();

  const [profile,    setProfile]    = useState(null);
  const [gami,       setGami]       = useState(null);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState('');
  const [toasts,     setToasts]     = useState([]);
  const [badgeModal, setBadgeModal] = useState(null);
  const [levelModal, setLevelModal] = useState(null);
  const [prevXp,     setPrevXp]     = useState(null);
  const toastTimer = useRef(null);

  const addToast = (type, message) => {
    const id = ++toastIdCounter;
    setToasts(prev => [...prev, { id, type, message }]);
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), 4500);
  };
  const removeToast = (id) => setToasts(prev => prev.filter(t => t.id !== id));

const fetchAll = (userId) => {
    setLoading(true);
    // Profile now comes from the JWT-based GET /api/profile (ProfileDto — no
    // userId). Gamification summary is still fetched by userId because the
    // backend GamificationController still requires the ?userId= param.
    Promise.all([
      getUserProfile(),
      getGamificationSummary().catch(() => null),
    ])
      .then(([profileRes, gamiRes]) => {
        const p = profileRes.data;
        setPrevXp(prev => prev === null ? p.xp : prev);
        setProfile(p);
        setError('');
        saveUser({ ...user, xp: p.xp, level: p.level, financialScore: p.financialScore });
        if (gamiRes) setGami(gamiRes.data);
      })
      .catch(() => setError('Could not load profile. Is the backend running on port 8080?'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    const uid = user?.userId;
    if (!uid) { setError('No user session found. Please log in again.'); setLoading(false); return; }
    fetchAll(uid);
  }, []);

  useEffect(() => {
    if (!lastQuizResult || !user?.userId) return;
    const oldXp = profile?.xp ?? user?.xp ?? 0;
    setPrevXp(oldXp);
    fetchAll(user.userId);

    const r = lastQuizResult;
    const newBadges = r.badgesAwarded ?? [];

    if (newBadges.length > 0) {
      setBadgeModal(newBadges[0]);
      const msg = `Fantastic ${user.name?.split(' ')[0] || ''}! You've unlocked the ${newBadges[0].badgeName} badge!`;
    } else if (r.leveledUp) {
      setLevelModal({ level: r.level, xpEarned: r.xpEarned });
      const msg = `Congratulations ${user.name?.split(' ')[0] || ''}! You've reached Level ${r.level}!`;
    } else if (r.currentStreak > 1) {
      const msg = `${r.currentStreak}-day streak! You're on fire! 🔥`;
    }

    // Toasts
    if (r.xpEarned > 0) addToast('xp', `⚡ +${r.xpEarned} XP earned!`);
    if ((r.coinsEarned ?? 0) > 0) addToast('coin', `🪙 +${r.coinsEarned} coins!`);
    if (newBadges.length > 0) addToast('badge', `🏆 Badge unlocked: ${newBadges[0].badgeName}`);
    if (r.leveledUp) addToast('levelup', `🎊 Level Up! You're now Level ${r.level}!`);
    if (r.achievementsUnlocked?.length > 0) addToast('success', `✨ Achievement: ${r.achievementsUnlocked[0].name}`);
  }, [lastQuizResult]);

  useEffect(() => () => { if (toastTimer.current) clearTimeout(toastTimer.current); }, []);

  const xpToNext    = gami?.xpToNextLevel  ?? Math.max(0, (profile?.level ?? 1) * 100 - (profile?.xp ?? 0));
  const xpProgress  = gami?.progressPercent ?? 0;
  const coins       = gami?.coins          ?? profile?.coins ?? 0;
  const curStreak   = gami?.currentStreak  ?? 0;
  const longestStreak = gami?.longestStreak ?? 0;
  const badges      = gami?.badges         ?? [];
  const achievements = gami?.achievements  ?? [];
  const firstName   = profile?.name?.split(' ')[0] ?? user?.name?.split(' ')[0] ?? '…';

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">

      {/* Toast stack */}
      <Toast toasts={toasts} onRemove={removeToast} />

      {/* ── Badge unlock modal ─────────────────────────────────────────────── */}
      <Modal open={!!badgeModal} onClose={() => setBadgeModal(null)}>
        <div className="bg-slate-900 border border-violet-500/60 rounded-2xl p-8 text-center shadow-2xl">
          <div
            className="text-7xl mb-4 inline-block"
            style={{ animation: 'badge-pop 0.5s cubic-bezier(0.34,1.56,0.64,1) forwards' }}
          >
            {badgeModal?.icon ?? '🏅'}
          </div>
          <div className="text-violet-400 text-xs font-bold tracking-widest uppercase mb-2">Badge Unlocked!</div>
          <h2 className="text-white text-2xl font-bold mb-2">{badgeModal?.badgeName}</h2>
          {badgeModal?.description && (
            <p className="text-slate-400 text-sm mb-4">{badgeModal.description}</p>
          )}
          <button
            onClick={() => setBadgeModal(null)}
            className="btn-primary px-8 py-2.5 rounded-xl text-sm"
          >
            Awesome! 🎉
          </button>
        </div>
      </Modal>

      {/* ── Level-up modal ─────────────────────────────────────────────────── */}
      <Modal open={!!levelModal} onClose={() => setLevelModal(null)}>
        <div className="bg-slate-900 border border-amber-500/60 rounded-2xl p-8 text-center shadow-2xl overflow-hidden relative">
          {/* Glow bg */}
          <div className="absolute inset-0 bg-gradient-to-b from-amber-500/5 to-transparent pointer-events-none" />
          <div className="text-5xl mb-3" style={{ animation: 'bounceIn 0.5s ease' }}>✨</div>
          <div className="text-amber-400 text-xs font-bold tracking-widest uppercase mb-3">Level Up!</div>
          <div
            className="text-6xl font-black text-white mb-2"
            style={{ animation: 'scaleIn 0.4s 0.1s cubic-bezier(0.34,1.56,0.64,1) both' }}
          >
            Level {levelModal?.level}
          </div>
          {levelModal?.xpEarned > 0 && (
            <div className="text-indigo-300 font-semibold mb-3">+{levelModal.xpEarned} XP</div>
          )}
          <p className="text-slate-400 text-sm mb-6">You're getting better with every decision.</p>
          <button
            onClick={() => setLevelModal(null)}
            className="btn-primary px-8 py-2.5 rounded-xl text-sm"
          >
            Keep Going! 🚀
          </button>
        </div>
      </Modal>

      {/* ── Hero section ───────────────────────────────────────────────────── */}
      <div
        className="mb-8 rounded-2xl p-6 border border-slate-700/60 relative overflow-hidden"
        style={{
          background: 'linear-gradient(135deg, rgba(79,70,229,0.12) 0%, rgba(139,92,246,0.08) 50%, rgba(15,23,42,0) 100%)',
          animation: 'fadeSlideUp 0.4s ease',
        }}
      >
        <div className="absolute top-0 right-0 w-64 h-64 rounded-full opacity-5 pointer-events-none"
          style={{ background: 'radial-gradient(circle, #6366f1, transparent)', transform: 'translate(30%, -30%)' }} />
        <h1 className="text-3xl font-bold text-white mb-1">
          Welcome back, <span style={{ color: '#818cf8' }}>{firstName}</span> 👋
        </h1>
        <p className="text-slate-400">Here's your financial progress overview</p>
        {curStreak > 0 && (
          <div className="mt-3 inline-flex items-center gap-2 bg-orange-950/40 border border-orange-500/30 rounded-full px-4 py-1.5 text-sm">
            <span className="flame">🔥</span>
            <span className="text-orange-300 font-semibold">{curStreak}-day streak!</span>
            {longestStreak > curStreak && (
              <span className="text-slate-500 text-xs">Best: {longestStreak}</span>
            )}
          </div>
        )}
      </div>

      {/* ── Loading ─────────────────────────────────────────────────────────── */}
      {loading && (
        <>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
            {[0,1,2,3].map(i => <SkeletonStatCard key={i} />)}
          </div>
          <SkeletonCard lines={2} className="mb-6" />
          <div className="grid md:grid-cols-2 gap-6">
            <SkeletonCard lines={4} />
            <SkeletonCard lines={4} />
          </div>
        </>
      )}

      {/* ── Error ───────────────────────────────────────────────────────────── */}
      {!loading && error && (
        <div className="bg-red-950/60 border border-red-500/40 rounded-xl p-8 text-center"
          style={{ animation: 'fadeSlideUp 0.3s ease' }}>
          <div className="text-5xl mb-3">⚠️</div>
          <p className="text-red-300 font-medium mb-4">{error}</p>
          <button onClick={() => fetchAll(user?.userId)} className="btn-secondary px-6 py-2 rounded-lg text-sm">
            Retry
          </button>
        </div>
      )}

      {/* ── Main content ────────────────────────────────────────────────────── */}
      {!loading && !error && profile && (
        <>
          {/* ── Stat cards ─────────────────────────────────────────────────── */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
            {[
              {
                icon: '🎯', label: 'Total XP', accent: '#818cf8',
                value: <CountUp from={prevXp ?? profile.xp} to={profile.xp} duration={900} suffix=" XP" className="text-3xl font-black" />,
                sub: `${xpToNext} XP to next level`,
                delay: 0,
              },
              {
                icon: '⚡', label: 'Level', accent: '#fbbf24',
                value: <span className="text-3xl font-black text-amber-400">Level {profile.level}</span>,
                sub: 'Keep completing quizzes!',
                delay: 50,
              },
              {
                icon: '🪙', label: 'Coins', accent: '#fcd34d',
                value: <CountUp from={0} to={coins} duration={700} className="text-3xl font-black text-yellow-400" />,
                sub: 'Earn more by passing quizzes',
                delay: 100,
              },
              {
                icon: '🔥', label: 'Streak', accent: '#fb923c',
                value: <span className="text-3xl font-black text-orange-400">{curStreak} <span className="text-lg">days</span></span>,
                sub: `Best: ${longestStreak} days`,
                delay: 150,
              },
            ].map(({ icon, label, accent, value, sub, delay }) => (
              <div
                key={label}
                className="glow-card bg-slate-800/90 border border-slate-700/80 rounded-xl p-5
                           hover:border-indigo-500/40 transition-all duration-300 fade-slide-up"
                style={{ animationDelay: `${delay}ms` }}
              >
                <div className="flex items-center gap-2 mb-2">
                  <span className="text-2xl">{icon}</span>
                  <span className="text-slate-400 text-xs font-semibold uppercase tracking-wide">{label}</span>
                </div>
                {value}
                {sub && <div className="text-slate-500 text-xs mt-1">{sub}</div>}
              </div>
            ))}
          </div>

          {/* ── XP Progress bar ────────────────────────────────────────────── */}
          <div
            className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5 mb-6 fade-slide-up anim-delay-3"
          >
            <div className="flex items-center justify-between mb-3">
              <div>
                <h2 className="text-white font-bold">XP Progress</h2>
                <p className="text-slate-500 text-xs mt-0.5">Level {profile.level} → Level {profile.level + 1}</p>
              </div>
              <span className="text-slate-400 text-sm font-medium">{xpToNext} XP to go</span>
            </div>
            <div className="w-full bg-slate-700/80 rounded-full h-3.5 overflow-hidden">
              <div
                className="h-full rounded-full progress-fill-animated"
                style={{
                  width: `${xpProgress}%`,
                  background: 'linear-gradient(90deg, #6366f1, #8b5cf6, #f59e0b)',
                  boxShadow: '0 0 12px rgba(99,102,241,0.5)',
                }}
              />
            </div>
            <div className="flex justify-between mt-2">
              <span className="text-slate-500 text-xs">{xpProgress}% complete</span>
              <span className="text-indigo-400 text-xs font-semibold">{profile.xp} XP total</span>
            </div>
          </div>

          {/* ── Badges + Achievements ──────────────────────────────────────── */}
          <div className="grid md:grid-cols-2 gap-6 mb-6">

            {/* Badges */}
            <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5 fade-slide-up anim-delay-4">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-white font-bold flex items-center gap-2">
                  🏅 Badges
                  <span className="bg-violet-900/60 border border-violet-500/40 text-violet-300 text-xs px-2 py-0.5 rounded-full">
                    {badges.length}
                  </span>
                </h2>
              </div>
              {badges.length === 0 ? (
                <div className="text-center py-6">
                  <div className="text-4xl mb-2 opacity-30">🏅</div>
                  <p className="text-slate-500 text-sm">Pass quizzes to earn badges!</p>
                </div>
              ) : (
                <div className="flex flex-wrap gap-2">
                  {badges.map((b, i) => (
                    <span
                      key={b.badgeCode}
                      className="badge-unlocked"
                      style={{ animationDelay: `${i * 60}ms` }}
                      title={b.description ?? b.badgeName}
                    >
                      <span>{b.icon}</span> {b.badgeName}
                    </span>
                  ))}
                </div>
              )}
            </div>

            {/* Achievements */}
            <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5 fade-slide-up anim-delay-5">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-white font-bold flex items-center gap-2">
                  🏆 Achievements
                  <span className="bg-indigo-900/60 border border-indigo-500/40 text-indigo-300 text-xs px-2 py-0.5 rounded-full">
                    {achievements.length}
                  </span>
                </h2>
              </div>
              {achievements.length === 0 ? (
                <div className="text-center py-6">
                  <div className="text-4xl mb-2 opacity-30">🏆</div>
                  <p className="text-slate-500 text-sm">Complete quizzes to earn achievements!</p>
                </div>
              ) : (
                <div className="flex flex-wrap gap-2">
                  {achievements.map((a, i) => (
                    <span
                      key={a.code}
                      title={`${a.description ?? ''}`}
                      className="badge-unlocked cursor-help"
                      style={{
                        background: 'linear-gradient(135deg, rgba(79,70,229,0.25), rgba(99,102,241,0.15))',
                        borderColor: 'rgba(99,102,241,0.5)',
                        color: '#a5b4fc',
                        animationDelay: `${i * 60}ms`,
                      }}
                    >
                      <span>{a.icon}</span> {a.name}
                    </span>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* ── Financial Score + Daily Goal ───────────────────────────────── */}
          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5 fade-slide-up anim-delay-5">
              <h2 className="text-white font-bold mb-4">💹 Financial Score</h2>
              <div className="flex items-end gap-3 mb-3">
                <CountUp
                  from={0}
                  to={profile.financialScore}
                  duration={1000}
                  className="text-5xl font-black text-emerald-400"
                />
                <span className="text-slate-400 text-sm mb-1">/ 100</span>
              </div>
              <div className="w-full bg-slate-700/80 rounded-full h-2.5 overflow-hidden">
                <div
                  className="h-full rounded-full progress-fill-animated"
                  style={{
                    width: `${profile.financialScore}%`,
                    background: 'linear-gradient(90deg, #10b981, #34d399)',
                    boxShadow: '0 0 8px rgba(16,185,129,0.4)',
                  }}
                />
              </div>
              <p className="text-slate-500 text-xs mt-2">
                {profile.financialScore >= 80 ? '🌟 Excellent financial health!' :
                 profile.financialScore >= 60 ? '📈 Good progress, keep going!' :
                 '💪 Keep learning to improve your score!'}
              </p>
            </div>

            <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5 fade-slide-up anim-delay-6">
              <h2 className="text-white font-bold mb-4">📅 Daily Goals</h2>
              <div className="space-y-3">
                {[
                  {
                    label: 'Complete a quiz',
                    done: !!lastQuizResult,
                    doneText: '✅ Done',
                    pendingText: '⬜ Pending',
                  },
                  {
                    label: 'Maintain streak',
                    done: curStreak > 0,
                    doneText: `✅ ${curStreak} days`,
                    pendingText: '⬜ Pending',
                  },
                  {
                    label: 'Earn XP today',
                    done: (lastQuizResult?.xpEarned ?? 0) > 0,
                    doneText: `✅ +${lastQuizResult?.xpEarned ?? 0} XP`,
                    pendingText: '⬜ Pending',
                  },
                ].map(({ label, done, doneText, pendingText }) => (
                  <div key={label} className="flex items-center justify-between text-sm">
                    <span className="text-slate-300">{label}</span>
                    <span className={`font-semibold text-xs px-2.5 py-1 rounded-full ${
                      done
                        ? 'bg-emerald-900/40 text-emerald-400 border border-emerald-500/30'
                        : 'bg-slate-700/60 text-slate-500 border border-slate-600/40'
                    }`}>
                      {done ? doneText : pendingText}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
