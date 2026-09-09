// src/components/mentor/MentorDialogue.jsx
// Accessible speech bubble card displaying mentor guidance, advice, and interactive controls.

import React from 'react';

export default function MentorDialogue({
  name = 'Alex',
  message = '',
  isSpeaking = false,
  muted = false,
  onDismiss,
  onReplay,
  onToggleMute,
  onOpenSettings,
  onMinimize,
}) {
  if (!message) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      className="relative max-w-xs sm:max-w-sm rounded-2xl border p-4 shadow-2xl transition-all duration-300 pointer-events-auto"
      style={{
        background: 'linear-gradient(135deg, rgba(15,23,42,0.96), rgba(30,41,59,0.96))',
        borderColor: isSpeaking ? 'rgba(99,102,241,0.8)' : 'rgba(71,85,105,0.6)',
        boxShadow: isSpeaking
          ? '0 10px 30px rgba(99,102,241,0.3)'
          : '0 10px 25px rgba(2,6,23,0.6)',
      }}
    >
      {/* Header: Mentor Name + Action Buttons */}
      <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-700/60 text-xs">
        <div className="flex items-center gap-1.5 font-bold text-indigo-300">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          <span>{name} — Finance Mentor</span>
        </div>

        <div className="flex items-center gap-1">
          {/* Replay speech */}
          <button
            onClick={onReplay}
            title="Replay speech"
            aria-label="Replay speech"
            className="p-1 rounded-md text-slate-400 hover:text-indigo-300 hover:bg-slate-700/60 transition-colors"
          >
            🔊
          </button>

          {/* Mute toggle */}
          <button
            onClick={onToggleMute}
            title={muted ? 'Unmute mentor voice' : 'Mute mentor voice'}
            aria-label={muted ? 'Unmute mentor voice' : 'Mute mentor voice'}
            className="p-1 rounded-md text-slate-400 hover:text-amber-300 hover:bg-slate-700/60 transition-colors"
          >
            {muted ? '🔇' : '🔔'}
          </button>

          {/* Settings */}
          <button
            onClick={onOpenSettings}
            title="Mentor settings"
            aria-label="Mentor settings"
            className="p-1 rounded-md text-slate-400 hover:text-white hover:bg-slate-700/60 transition-colors"
          >
            ⚙️
          </button>

          {/* Minimize */}
          <button
            onClick={onMinimize}
            title="Minimize mentor"
            aria-label="Minimize mentor"
            className="p-1 rounded-md text-slate-400 hover:text-white hover:bg-slate-700/60 transition-colors"
          >
            ▾
          </button>

          {/* Dismiss */}
          <button
            onClick={onDismiss}
            title="Dismiss message"
            aria-label="Dismiss message"
            className="p-1 rounded-md text-slate-400 hover:text-rose-400 hover:bg-slate-700/60 transition-colors"
          >
            ✕
          </button>
        </div>
      </div>

      {/* Message Body */}
      <p className="text-slate-200 text-xs sm:text-sm leading-relaxed font-medium select-text">
        {message}
      </p>

      {/* Speech Indicator */}
      {isSpeaking && (
        <div className="flex items-center gap-1 mt-2.5 text-[10px] text-indigo-400 font-semibold">
          <span className="inline-block w-1.5 h-1.5 rounded-full bg-indigo-400 animate-ping" />
          <span>Speaking...</span>
        </div>
      )}
    </div>
  );
}
