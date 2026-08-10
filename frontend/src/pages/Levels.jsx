<<<<<<< HEAD
// Levels.jsx — Premium Level Map (Learning Path 2.0)
// Levels are loaded from the backend tiered API — never hardcoded in React.
import { useState, useEffect } from 'react';
import { useNavigate }  from 'react-router-dom';
import { getLearningPath } from '../api/endpoints';

const TIER_META = {
  BEGINNER:     { title: 'Beginner',     icon: '🌱', color: '#10b981', desc: 'Build your financial foundation' },
  INTERMEDIATE: { title: 'Intermediate', icon: '📈', color: '#f59e0b', desc: 'Grow your money skills' },
  ADVANCED:     { title: 'Advanced',     icon: '🚀', color: '#f43f5e', desc: 'Master advanced finance' },
};

function LevelCard({ lvl, index, onClick }) {
  const status = (lvl.status ?? 'LOCKED').toUpperCase();
  const isCompleted = status === 'COMPLETED';
  const isCurrent   = status === 'UNLOCKED' || status === 'IN_PROGRESS';
  const isLocked    = status === 'LOCKED';

  return (
    <div className="fade-slide-up" style={{ animationDelay: `${index * 40}ms` }}>
      <div
        onClick={!isLocked ? onClick : undefined}
        className={`relative rounded-2xl border p-5 transition-all duration-300 select-none h-full
          ${isCompleted
            ? 'bg-emerald-950/40 border-emerald-500/50 cursor-pointer hover:-translate-y-1 hover:shadow-lg hover:shadow-emerald-900/40 hover:border-emerald-400/70'
            : isCurrent
              ? 'border-indigo-500/70 cursor-pointer hover:-translate-y-1 hover:shadow-xl hover:shadow-indigo-900/50'
              : 'bg-slate-800/40 border-slate-700/50 opacity-60 cursor-not-allowed'
          }`}
        style={isCurrent ? {
          background: 'linear-gradient(135deg, rgba(79,70,229,0.2), rgba(139,92,246,0.12))',
        } : {}}
      >
        {/* Top row: level number + status icon */}
        <div className="flex items-center justify-between mb-3">
          <span className={`text-xs font-bold px-2.5 py-1 rounded-full
            ${isCompleted ? 'bg-emerald-600/80 text-white'
              : isCurrent ? 'bg-indigo-600/80 text-white'
              : 'bg-slate-700/80 text-slate-500'}`}>
            Level {lvl.levelNumber}
          </span>
          <span className="text-xl">
            {isCompleted ? '✅' : isCurrent ? '⭐' : '🔒'}
          </span>
        </div>

        {/* Title */}
        <h3 className={`font-bold text-sm leading-tight mb-2
          ${isCompleted ? 'text-emerald-100' : isCurrent ? 'text-white' : 'text-slate-500'}`}>
          {lvl.title}
        </h3>

        {/* Difficulty */}
        <span className={`inline-block text-[10px] font-semibold px-2 py-0.5 rounded-full mb-2
          ${lvl.difficulty === 'HARD' ? 'bg-rose-900/50 text-rose-300 border border-rose-500/40'
            : lvl.difficulty === 'MEDIUM' ? 'bg-amber-900/50 text-amber-300 border border-amber-500/40'
            : 'bg-emerald-900/50 text-emerald-300 border border-emerald-500/40'}`}>
          {lvl.difficulty}
        </span>

        {/* Footer: XP / coins / time */}
        <div className="flex flex-col gap-1 mt-2 text-[11px]">
          <span className="text-amber-500/80 font-medium">⚡ {lvl.xpReward} XP</span>
          <span className="text-yellow-500/70 font-medium">🪙 {lvl.coinReward} coins</span>
          <span className="text-slate-500">⏱️ {lvl.estimatedMinutes} min</span>
          {lvl.bestScore > 0 && (
            <span className="text-indigo-400 font-semibold">Best: {lvl.bestScore}%</span>
          )}
        </div>

        {/* Status CTA */}
        <div className="mt-3 pt-3 border-t border-slate-700/40">
          <span className={`text-xs font-semibold
            ${isCompleted ? 'text-emerald-400'
              : isCurrent ? 'text-indigo-300'
              : 'text-slate-500'}`}>
            {isCompleted ? '✓ Completed' : isCurrent ? '▶ Start / Continue' : '🔒 Locked'}
          </span>
        </div>
      </div>
    </div>
  );
}

export default function Levels() {
  const navigate    = useNavigate();
  const [path,   setPath]   = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState('');

  useEffect(() => {
    getLearningPath()
      .then(({ data }) => setPath(data))
      .catch(() => setError('Could not load the learning path. Is the backend running on port 8080?'))
      .finally(() => setLoading(false));
  }, []);

  const allLevels = path?.tiers?.flatMap(t => t.levels ?? []) ?? [];
  const completedCount = allLevels.filter(l => (l.status ?? '').toUpperCase() === 'COMPLETED').length;
  const unlockedCount  = allLevels.filter(l => ['UNLOCKED', 'IN_PROGRESS'].includes((l.status ?? '').toUpperCase())).length;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="mb-6 fade-slide-up">
        <h1 className="text-3xl font-bold text-white">🗺️ Level Map</h1>
        <p className="text-slate-400 mt-1">Your 36-level financial learning journey</p>
      </div>

=======
// Levels.jsx
// Fetches level data from GET /api/levels, merges with local definitions,
// and renders clickable cards. Unlocked cards navigate to /quiz?level=N.

import { useState, useEffect } from 'react';
import { useNavigate }  from 'react-router-dom';
import api              from '../services/api';
import { useAuth }      from '../context/AuthContext';
import LevelCard        from '../components/LevelCard';

const LOCAL_LEVELS = [
  { level: 1,  title: 'Money Basics',       description: 'Learn what money is and how it works in daily life.'           },
  { level: 2,  title: 'Budgeting 101',       description: 'Track income and expenses to build your first budget.'         },
  { level: 3,  title: 'Smart Saving',        description: 'Discover saving strategies and build an emergency fund.'       },
  { level: 4,  title: 'Debt & Credit',       description: 'Understand credit scores, loans, and managing debt wisely.'    },
  { level: 5,  title: 'Investing Basics',    description: 'Introduction to stocks, bonds, and mutual funds.'              },
  { level: 6,  title: 'Stock Market',        description: 'How the stock market works and how to read charts.'            },
  { level: 7,  title: 'Tax Planning',        description: 'Learn income tax slabs, deductions, and how to file returns.'  },
  { level: 8,  title: 'Mutual Funds',        description: 'SIPs, NAV, and how to pick the right mutual fund.'            },
  { level: 9,  title: 'Retirement Planning', description: 'Plan for the future with PPF, NPS, and pension schemes.'      },
  { level: 10, title: 'Financial Freedom',   description: 'Build passive income streams and achieve financial independence.' },
];

export default function Levels() {
  const { user }    = useAuth();
  const navigate    = useNavigate();

  const [levels,  setLevels]  = useState([]);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState('');

  // A level is unlocked when the user's current level >= that level number
  const currentLevel = user?.level ?? 1;

  useEffect(() => {
    api.get('/levels')
      .then(({ data }) => {
        // Build xpRequired lookup from backend response
        const xpMap = {};
        data.forEach(e => { xpMap[e.level] = e.xpRequired; });

        setLevels(LOCAL_LEVELS.map(l => ({
          ...l,
          xpRequired: xpMap[l.level] ?? l.level * 100,
          unlocked:   l.level <= currentLevel,
        })));
      })
      .catch(() => {
        // Backend unreachable — use local fallback so page still renders
        setLevels(LOCAL_LEVELS.map(l => ({
          ...l,
          xpRequired: l.level * 100,
          unlocked:   l.level <= currentLevel,
        })));
        setError('Backend unreachable — showing local level data.');
      })
      .finally(() => setLoading(false));
  }, [currentLevel]);

  // Navigate to the quiz for the selected level
  const handleLevelClick = (levelNumber) => {
    navigate(`/quiz?level=${levelNumber}`);
  };

  const unlockedCount = levels.filter(l => l.unlocked).length;

  return (
    <div className="max-w-6xl mx-auto px-4 py-10">

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">🗺️ Level Map</h1>
        <p className="text-slate-400 mt-1">
          Click an unlocked level to start its quiz
        </p>
      </div>

      {/* Soft error banner */}
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
      {error && (
        <div className="bg-amber-950/50 border border-amber-500/40 text-amber-300
                        text-sm rounded-xl px-4 py-3 mb-6 flex items-center gap-2">
          <span>⚠️</span><span>{error}</span>
        </div>
      )}

<<<<<<< HEAD
      {loading && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500 border-t-transparent animate-spin" />
=======
      {/* Loading */}
      {loading && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500
                          border-t-transparent animate-spin" />
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
          <p className="text-slate-400 text-sm">Loading levels…</p>
        </div>
      )}

<<<<<<< HEAD
      {!loading && path && (
        <>
          {/* Summary bar */}
          <div className="bg-slate-800/90 border border-slate-700/80 rounded-xl p-5 mb-8 fade-slide-up
                          flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-xl flex items-center justify-center text-2xl"
                style={{ background: 'linear-gradient(135deg, #4f46e5, #7c3aed)' }}>
                🎯
              </div>
              <div>
                <p className="text-slate-400 text-xs font-semibold uppercase tracking-wide">Learning Progress</p>
                <p className="text-white font-black text-xl">
                  {completedCount} / {allLevels.length} completed
                </p>
              </div>
            </div>
            <div className="flex items-center gap-2 flex-wrap">
              <span className="bg-emerald-900/60 border border-emerald-500/40 text-emerald-300 text-sm px-3 py-1.5 rounded-full font-semibold">
                ✅ {completedCount} Completed
              </span>
              <span className="bg-indigo-900/60 border border-indigo-500/40 text-indigo-300 text-sm px-3 py-1.5 rounded-full font-semibold">
                ⭐ {unlockedCount} Available
              </span>
              <span className="bg-slate-700/60 border border-slate-600/40 text-slate-400 text-sm px-3 py-1.5 rounded-full">
                🔒 {allLevels.length - completedCount - unlockedCount} Locked
=======
      {!loading && (
        <>
          {/* Summary bar */}
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-5 mb-8
                          flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <span className="text-white font-semibold">Your Level: </span>
              <span className="text-emerald-400 font-bold text-lg">Level {currentLevel}</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="bg-emerald-900/60 border border-emerald-500/40
                               text-emerald-300 text-sm px-3 py-1 rounded-full">
                ✅ {unlockedCount} Unlocked
              </span>
              <span className="bg-slate-700 border border-slate-600
                               text-slate-400 text-sm px-3 py-1 rounded-full">
                🔒 {levels.length - unlockedCount} Locked
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
              </span>
            </div>
          </div>

<<<<<<< HEAD
          {/* Tiers */}
          {path.tiers.map(tier => {
            const meta = TIER_META[tier.tier] ?? TIER_META.BEGINNER;
            return (
              <div key={tier.tier} className="mb-10">
                {/* Tier header */}
                <div className="flex items-center gap-3 mb-4 fade-slide-up">
                  <span className="text-2xl">{meta.icon}</span>
                  <div>
                    <h2 className="text-xl font-bold text-white" style={{ color: meta.color }}>
                      {tier.title}
                    </h2>
                    <p className="text-slate-400 text-xs">{meta.desc} — {tier.levels.length} levels</p>
                  </div>
                  <div className="flex-1 h-px bg-slate-700/60 ml-2" />
                </div>

                {/* Level grid */}
                <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
                  {(tier.levels ?? []).map((lvl, index) => (
                    <LevelCard
                      key={lvl.levelNumber}
                      lvl={lvl}
                      index={index}
                      onClick={() => navigate(`/quiz?level=${lvl.levelNumber}`)}
                    />
                  ))}
                </div>
              </div>
            );
          })}
=======
          {/* Level cards grid */}
          <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-5 gap-4">
            {levels.map(lvl => (
              <LevelCard
                key={lvl.level}
                level={lvl.level}
                title={lvl.title}
                description={lvl.description}
                xpRequired={lvl.xpRequired}
                unlocked={lvl.unlocked}
                // Pass click handler — LevelCard only calls it when unlocked
                onClick={() => handleLevelClick(lvl.level)}
              />
            ))}
          </div>
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
        </>
      )}
    </div>
  );
}
