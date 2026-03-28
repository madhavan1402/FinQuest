// ProgressBar — reusable animated bar.
// Props:
//   value   : current value (number)
//   max     : maximum value (number)
//   label   : text shown above the bar (string)
//   color   : Tailwind bg class, defaults to indigo gradient
export default function ProgressBar({ value, max, label, color = 'xp-bar' }) {
  const pct = Math.min(Math.round((value / max) * 100), 100);

  return (
    <div className="w-full">
      {label && (
        <div className="flex justify-between text-xs text-slate-400 mb-1">
          <span>{label}</span>
          <span>{value} / {max}</span>
        </div>
      )}
      {/* Track */}
      <div className="w-full bg-slate-700 rounded-full h-3 overflow-hidden">
        {/* Fill — width driven by percentage, animated via CSS transition */}
        <div
          className={`h-full rounded-full ${color} transition-all duration-700 ease-out`}
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  );
}
