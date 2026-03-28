// StatCard — a glowing info tile used on the Dashboard.
// Props: icon (emoji), label (string), value (string|number), sub (string, optional)
export default function StatCard({ icon, label, value, sub, accent = 'text-indigo-400' }) {
  return (
    <div className="glow-card bg-slate-800 border border-slate-700 rounded-xl p-5
                    hover:border-indigo-500/60 transition-all duration-300">
      <div className="flex items-center gap-3 mb-2">
        <span className="text-2xl">{icon}</span>
        <span className="text-slate-400 text-sm">{label}</span>
      </div>
      <div className={`text-3xl font-bold ${accent}`}>{value}</div>
      {sub && <div className="text-slate-500 text-xs mt-1">{sub}</div>}
    </div>
  );
}
