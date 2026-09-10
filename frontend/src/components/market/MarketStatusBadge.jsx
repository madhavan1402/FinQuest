// src/components/market/MarketStatusBadge.jsx
import React from 'react';

export default function MarketStatusBadge({ status, isSimulated }) {
  const s = (status || 'UNKNOWN').toUpperCase();

  let colorClasses = 'bg-slate-800/80 text-slate-300 border-slate-700';
  let dotColor = 'bg-slate-400';
  let label = s;

  if (s === 'OPEN') {
    colorClasses = 'bg-emerald-950/60 text-emerald-300 border-emerald-500/40';
    dotColor = 'bg-emerald-400 animate-pulse';
    label = 'Market Open';
  } else if (s === 'PRE_OPEN') {
    colorClasses = 'bg-amber-950/60 text-amber-300 border-amber-500/40';
    dotColor = 'bg-amber-400 animate-pulse';
    label = 'Pre-Open';
  } else if (s === 'CLOSED') {
    colorClasses = 'bg-rose-950/40 text-rose-300 border-rose-500/30';
    dotColor = 'bg-rose-400';
    label = 'Market Closed';
  } else if (s === 'HOLIDAY') {
    colorClasses = 'bg-indigo-950/60 text-indigo-300 border-indigo-500/40';
    dotColor = 'bg-indigo-400';
    label = 'Exchange Holiday';
  }

  return (
    <div className="inline-flex items-center gap-2">
      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${colorClasses}`}>
        <span className={`w-2 h-2 rounded-full ${dotColor}`} />
        {label}
      </span>
      {isSimulated && (
        <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-slate-800/90 text-amber-400 border border-amber-500/30">
          Simulated Data
        </span>
      )}
    </div>
  );
}
