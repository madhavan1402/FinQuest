// src/components/mentor/MentorFallback.jsx
// High-quality CSS/SVG 2D animated mentor avatar fallback.
// Rendered when WebGL is unavailable, the 3D canvas is loading, or GLB assets do not exist yet.
// Distinct male (Alex) and female (Maya) visuals with dynamic emotional indicators.

import React from 'react';

const EMOTION_BADGES = {
  idle: '💼',
  talking: '🎙️',
  happy: '✨',
  sad: '💭',
  thinking: '🤔',
  celebrating: '🏆',
  encouraging: '🚀',
};

export default function MentorFallback({ gender = 'male', emotion = 'idle', isSpeaking = false }) {
  const isFemale = gender === 'female';
  const badge = EMOTION_BADGES[emotion] || '💼';

  return (
    <div className="relative w-28 h-28 mx-auto select-none flex items-center justify-center">
      {/* Speaking Soundwave Glow Ring */}
      {isSpeaking && (
        <div
          className="absolute inset-0 rounded-full animate-ping opacity-30"
          style={{ background: isFemale ? '#ec4899' : '#6366f1' }}
        />
      )}

      {/* Floating Avatar Outer Shell */}
      <div
        className="relative w-24 h-24 rounded-full p-1 shadow-2xl transition-transform duration-300 hover:scale-105"
        style={{
          background: isFemale
            ? 'linear-gradient(135deg, #ec4899, #8b5cf6, #3b82f6)'
            : 'linear-gradient(135deg, #6366f1, #3b82f6, #10b981)',
          boxShadow: isSpeaking
            ? `0 0 25px ${isFemale ? 'rgba(236,72,153,0.6)' : 'rgba(99,102,241,0.6)'}`
            : '0 8px 20px rgba(0,0,0,0.4)',
        }}
      >
        {/* Inner Avatar Portrait Frame */}
        <div className="w-full h-full rounded-full overflow-hidden bg-slate-900 flex items-center justify-center relative">
          <svg
            viewBox="0 0 100 100"
            className="w-full h-full"
            xmlns="http://www.w3.org/2000/svg"
          >
            {/* Background Gradient */}
            <defs>
              <linearGradient id={`bgGrad-${gender}`} x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stopColor={isFemale ? '#312e81' : '#1e1b4b'} />
                <stop offset="100%" stopColor={isFemale ? '#831843' : '#0f172a'} />
              </linearGradient>
            </defs>
            <circle cx="50" cy="50" r="50" fill={`url(#bgGrad-${gender})`} />

            {/* Suit / Torso */}
            <path
              d="M20 95 C25 70, 75 70, 80 95 Z"
              fill={isFemale ? '#4c1d95' : '#1e293b'}
            />
            {/* Shirt / Tie / Blouse */}
            <path
              d={isFemale ? 'M42 75 L50 90 L58 75 Z' : 'M45 74 L50 88 L55 74 Z'}
              fill={isFemale ? '#f472b6' : '#6366f1'}
            />
            {!isFemale && (
              <polygon points="48,74 52,74 51,84 49,84" fill="#f59e0b" />
            )}

            {/* Neck */}
            <rect x="44" y="60" width="12" height="15" rx="3" fill="#fbcfe8" />

            {/* Head */}
            <ellipse cx="50" cy="48" rx="20" ry="24" fill="#fde047" opacity="0.1" />
            <ellipse cx="50" cy="48" rx="19" ry="22" fill="#fde68a" />

            {/* Hair */}
            {isFemale ? (
              // Female Hair: Elegant Waves
              <g fill="#92400e">
                <path d="M30 45 C28 20, 72 20, 70 45 C65 24, 35 24, 30 45 Z" />
                <path d="M28 42 C24 55, 26 72, 32 78 C28 66, 30 50, 32 42 Z" />
                <path d="M72 42 C76 55, 74 72, 68 78 C72 66, 70 50, 68 42 Z" />
              </g>
            ) : (
              // Male Hair: Modern Side-Part
              <path
                d="M30 40 C30 22, 70 20, 72 38 C68 28, 40 26, 30 40 Z"
                fill="#451a03"
              />
            )}

            {/* Eyes */}
            <circle cx="43" cy="46" r="2.5" fill="#1e293b" />
            <circle cx="57" cy="46" r="2.5" fill="#1e293b" />
            <circle cx="44" cy="45" r="0.8" fill="#ffffff" />
            <circle cx="58" cy="45" r="0.8" fill="#ffffff" />

            {/* Eyebrows */}
            {emotion === 'thinking' ? (
              <>
                <path d="M40 40 Q43 38 46 41" stroke="#451a03" strokeWidth="1.5" fill="none" />
                <path d="M54 41 Q57 39 60 40" stroke="#451a03" strokeWidth="1.5" fill="none" />
              </>
            ) : emotion === 'sad' ? (
              <>
                <path d="M40 41 Q43 43 46 42" stroke="#451a03" strokeWidth="1.5" fill="none" />
                <path d="M54 42 Q57 43 60 41" stroke="#451a03" strokeWidth="1.5" fill="none" />
              </>
            ) : (
              <>
                <path d="M40 41 Q43 39 46 41" stroke="#451a03" strokeWidth="1.5" fill="none" />
                <path d="M54 41 Q57 39 60 41" stroke="#451a03" strokeWidth="1.5" fill="none" />
              </>
            )}

            {/* Mouth */}
            {isSpeaking ? (
              <ellipse cx="50" cy="58" rx="4" ry="3.5" fill="#b91c1c" className="animate-pulse" />
            ) : emotion === 'sad' ? (
              <path d="M46 60 Q50 56 54 60" stroke="#b91c1c" strokeWidth="1.5" fill="none" />
            ) : (
              <path d="M45 57 Q50 63 55 57" stroke="#b91c1c" strokeWidth="1.8" fill="none" />
            )}

            {/* Smart Glasses for Male Mentor */}
            {!isFemale && (
              <g stroke="#64748b" strokeWidth="1" fill="none" opacity="0.85">
                <rect x="38" y="42" width="10" height="8" rx="2" />
                <rect x="52" y="42" width="10" height="8" rx="2" />
                <line x1="48" y1="46" x2="52" y2="46" />
              </g>
            )}
          </svg>
        </div>

        {/* Dynamic Emotion Badge Pill */}
        <div
          className="absolute -bottom-1 -right-1 w-7 h-7 rounded-full bg-slate-900 border-2 border-indigo-400 flex items-center justify-center text-xs shadow-md"
          title={`Mentor state: ${emotion}`}
        >
          {badge}
        </div>
      </div>
    </div>
  );
}
