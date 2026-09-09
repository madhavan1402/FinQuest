// StatCard — premium glowing info tile used on the Dashboard.
// Props: icon, label, value, sub, accent (Tailwind text class)
export default function StatCard({ icon, label, value, sub, accent = 'text-indigo-400' }) {
  return (
    <div className="glow-card bg-slate-800/90 border border-slate-700/80 rounded-xl p-5
                    hover:border-indigo-500/40 transition-all duration-300 hover:-translate-y-0.5">
      <div className="flex items-center gap-2 mb-2">
        <span className="text-2xl">{icon}</span>
        <span className="text-slate-400 text-xs font-semibold uppercase tracking-wide">{label}</span>
      </div>
      <div className={`text-3xl font-black ${accent}`}>{value}</div>
      {sub && <div className="text-slate-500 text-xs mt-1">{sub}</div>}
    </div>
  );
}
