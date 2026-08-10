// src/components/SkeletonCard.jsx
// Reusable skeleton loaders for loading states.

export function SkeletonCard({ lines = 3, className = '' }) {
  return (
    <div className={`bg-slate-800/80 border border-slate-700/60 rounded-xl p-5 ${className}`}>
      <div className="skeleton h-4 w-1/3 mb-4 rounded" />
      {Array.from({ length: lines }).map((_, i) => (
        <div key={i} className={`skeleton h-3 rounded mb-2 ${i === lines - 1 ? 'w-2/3' : 'w-full'}`} />
      ))}
    </div>
  );
}

export function SkeletonStatCard() {
  return (
    <div className="bg-slate-800/80 border border-slate-700/60 rounded-xl p-5">
      <div className="flex items-center gap-3 mb-3">
        <div className="skeleton w-8 h-8 rounded-lg" />
        <div className="skeleton h-3 w-20 rounded" />
      </div>
      <div className="skeleton h-8 w-24 rounded mb-2" />
      <div className="skeleton h-2.5 w-32 rounded" />
    </div>
  );
}

export function SkeletonList({ rows = 5 }) {
  return (
    <div className="flex flex-col gap-3">
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="bg-slate-800/80 border border-slate-700/60 rounded-xl px-5 py-4 flex items-center gap-4">
          <div className="skeleton w-8 h-8 rounded-full flex-shrink-0" />
          <div className="flex-1">
            <div className="skeleton h-3.5 w-32 rounded mb-2" />
            <div className="skeleton h-2.5 w-24 rounded" />
          </div>
          <div className="skeleton h-4 w-16 rounded" />
        </div>
      ))}
    </div>
  );
}

export function SkeletonQuiz() {
  return (
    <div className="flex flex-col gap-5">
      {Array.from({ length: 3 }).map((_, i) => (
        <div key={i} className="bg-slate-800/80 border border-slate-700/60 rounded-xl p-5">
          <div className="skeleton h-4 w-3/4 rounded mb-5" />
          {Array.from({ length: 4 }).map((_, j) => (
            <div key={j} className="skeleton h-10 rounded-lg mb-2" />
          ))}
        </div>
      ))}
    </div>
  );
}
