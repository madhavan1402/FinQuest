// src/components/Toast.jsx
// Reusable toast notification — slide in from top, auto-dismiss.
// Usage: <Toast toasts={toasts} onRemove={id => ...} />
// Each toast: { id, type: 'success'|'error'|'info'|'xp'|'coin'|'badge', message }

const TYPE_STYLES = {
  success: 'bg-emerald-900/95 border-emerald-500/60 text-emerald-100',
  error:   'bg-red-900/95 border-red-500/60 text-red-100',
  info:    'bg-indigo-900/95 border-indigo-500/60 text-indigo-100',
  xp:      'bg-indigo-900/95 border-indigo-400/60 text-white',
  coin:    'bg-amber-900/95 border-amber-500/60 text-amber-100',
  badge:   'bg-violet-900/95 border-violet-500/60 text-violet-100',
  levelup: 'bg-amber-900/95 border-amber-400/70 text-white',
};

const TYPE_ICONS = {
  success: '✅', error: '❌', info: 'ℹ️',
  xp: '⚡', coin: '🪙', badge: '🏆', levelup: '🎊',
};

export default function Toast({ toasts = [], onRemove }) {
  if (!toasts.length) return null;
  return (
    <div
      className="fixed top-20 right-4 z-[9998] flex flex-col gap-2 max-w-xs w-full"
      aria-live="polite"
      aria-label="Notifications"
    >
      {toasts.map(t => (
        <div
          key={t.id}
          role="alert"
          className={`flex items-start gap-3 rounded-xl border px-4 py-3 shadow-2xl
            text-sm font-medium cursor-pointer select-none
            ${TYPE_STYLES[t.type] ?? TYPE_STYLES.info}`}
          style={{ animation: 'toastIn 0.25s ease forwards' }}
          onClick={() => onRemove(t.id)}
        >
          <span className="text-lg flex-shrink-0 mt-0.5">{TYPE_ICONS[t.type] ?? 'ℹ️'}</span>
          <span className="flex-1 leading-snug">{t.message}</span>
          <button
            onClick={e => { e.stopPropagation(); onRemove(t.id); }}
            className="text-current opacity-60 hover:opacity-100 text-base leading-none flex-shrink-0"
            aria-label="Dismiss"
          >×</button>
        </div>
      ))}
    </div>
  );
}
