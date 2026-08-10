<<<<<<< HEAD
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
=======
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
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
      {sub && <div className="text-slate-500 text-xs mt-1">{sub}</div>}
    </div>
  );
}
