
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
  const navigate      = useNavigate();
  const [path, setPath] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState('');

  useEffect(() => {
    let isMounted = true;
    getLearningPath()
      .then(({ data }) => {
        if (isMounted) setPath(data);
      })
      .catch(() => {
        if (isMounted) setError('Could not load the learning path. Is the backend running on port 8080?');
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const allLevels = path?.tiers?.flatMap(t => t.levels ?? []) ?? [];
  const completedCount = allLevels.filter(l => (l.status ?? '').toUpperCase() === 'COMPLETED').length;
  const unlockedCount  = allLevels.filter(l => ['UNLOCKED', 'IN_PROGRESS'].includes((l.status ?? '').toUpperCase())).length;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-6 fade-slide-up">
        <h1 className="text-3xl font-bold text-white">🗺️ Level Map</h1>
        <p className="text-slate-400 mt-1">Your 36-level financial learning journey</p>
      </div>

      {/* Soft error banner */}
      {error && (
        <div className="bg-amber-950/50 border border-amber-500/40 text-amber-300
                        text-sm rounded-xl px-4 py-3 mb-6 flex items-center gap-2">
          <span>⚠️</span><span>{error}</span>
        </div>
      )}

      {/* Loading state */}
      {loading && (
        <div className="flex flex-col items-center justify-center py-24 gap-4">
          <div className="w-10 h-10 rounded-full border-4 border-indigo-500 border-t-transparent animate-spin" />
          <p className="text-slate-400 text-sm">Loading level map…</p>
        </div>
      )}

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
                🔒 {Math.max(0, allLevels.length - completedCount - unlockedCount)} Locked
              </span>
            </div>
          </div>

          {/* Tiers */}
          {(path.tiers || []).map(tier => {
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
                    <p className="text-slate-400 text-xs">{meta.desc} — {(tier.levels || []).length} levels</p>
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
        </>
      )}
    </div>
  );
}
