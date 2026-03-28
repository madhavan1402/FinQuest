// LevelCard.jsx
// Displays one level tile. Unlocked cards are clickable and navigate to the quiz.
// Locked cards are visually dimmed and not interactive.
//
// Props:
//   level       : number  — level number (1–10)
//   title       : string  — level title
//   description : string  — one-line description
//   xpRequired  : number  — XP threshold
//   unlocked    : boolean — green + clickable if true, grey + disabled if false
//   onClick     : fn      — called when an unlocked card is clicked

export default function LevelCard({ level, title, description, xpRequired, unlocked, onClick }) {
  return (
    <div
      // Only attach onClick and pointer cursor when the level is unlocked
      onClick={unlocked ? onClick : undefined}
      className={`rounded-xl p-5 border transition-all duration-300 select-none
        ${unlocked
          ? 'bg-emerald-950/60 border-emerald-500/60 cursor-pointer ' +
            'hover:border-emerald-300 hover:bg-emerald-900/50 ' +
            'hover:shadow-xl hover:shadow-emerald-900/50 hover:-translate-y-0.5'
          : 'bg-slate-800/60 border-slate-700 opacity-50 cursor-not-allowed'
        }`}
    >
      {/* Top row: level badge + lock/unlock icon */}
      <div className="flex items-center justify-between mb-4">
        <span className={`text-xs font-bold px-2.5 py-1 rounded-full
          ${unlocked ? 'bg-emerald-600 text-white' : 'bg-slate-700 text-slate-400'}`}>
          Level {level}
        </span>
        <span className="text-xl">{unlocked ? '🔓' : '🔒'}</span>
      </div>

      {/* Title */}
      <h3 className={`font-semibold text-base mb-1
        ${unlocked ? 'text-white' : 'text-slate-500'}`}>
        {title}
      </h3>

      {/* Description */}
      <p className={`text-sm leading-snug
        ${unlocked ? 'text-slate-400' : 'text-slate-600'}`}>
        {description}
      </p>

      {/* Footer: status + XP */}
      <div className="mt-4 flex items-center justify-between">
        <span className={`text-xs font-semibold
          ${unlocked ? 'text-emerald-400' : 'text-slate-600'}`}>
          {unlocked ? '✅ Unlocked' : '🔒 Locked'}
        </span>
        <span className="text-xs text-amber-500/80 font-medium">⚡ {xpRequired} XP</span>
      </div>

      {/* "Start Quiz" hint — only visible on unlocked cards */}
      {unlocked && (
        <div className="mt-3 text-center text-xs text-emerald-400/70 font-medium">
          Click to start quiz →
        </div>
      )}
    </div>
  );
}
