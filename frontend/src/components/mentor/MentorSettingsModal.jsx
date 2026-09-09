import React from 'react';
import { useMentor } from '../../context/MentorContext';

export default function MentorSettingsModal({ isOpen, onClose }) {
  const {
    gender,
    setGender,
    muted,
    toggleMute,
    preferredVoice,
    setPreferredVoice,
    voices,
    visible,
    setVisible,
    isSpeaking,
    replaySpeech,
  } = useMentor();

  if (!isOpen) return null;

  const toggleVisibility = () => {
    setVisible((prev) => !prev);
  };


  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="mentor-settings-title"
      className="fixed inset-0 z-[9990] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-fade-in"
      onClick={onClose}
    >
      <div
        className="w-full max-w-md rounded-2xl bg-fq-surface border border-fq-border p-6 shadow-2xl relative text-fq-text"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between pb-4 border-b border-fq-border">
          <div className="flex items-center space-x-2">
            <span className="text-xl">⚙️</span>
            <h3 id="mentor-settings-title" className="text-lg font-bold text-white">
              Finance Mentor Settings
            </h3>
          </div>
          <button
            onClick={onClose}
            aria-label="Close mentor settings"
            className="rounded-lg p-1.5 text-fq-muted hover:bg-fq-card hover:text-white transition-colors"
          >
            ✕
          </button>
        </div>

        <div className="py-4 space-y-5">
          {/* Avatar Selection */}
          <div>
            <label className="block text-xs font-semibold text-fq-muted uppercase tracking-wider mb-2">
              Select Your Mentor
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setGender('male')}
                className={`flex items-center space-x-3 p-3 rounded-xl border transition-all text-left ${
                  gender === 'male'
                    ? 'border-cyan-500/80 bg-cyan-500/10 shadow-lg shadow-cyan-500/10'
                    : 'border-fq-border bg-fq-card/50 hover:border-fq-border-light'
                }`}
              >
                <div className="w-10 h-10 rounded-full bg-cyan-500/20 border border-cyan-500/40 flex items-center justify-center text-lg shrink-0">
                  👨‍💼
                </div>
                <div>
                  <div className="font-semibold text-white text-sm">Alex</div>
                  <div className="text-xs text-fq-muted">Analytical & Direct</div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => setGender('female')}
                className={`flex items-center space-x-3 p-3 rounded-xl border transition-all text-left ${
                  gender === 'female'
                    ? 'border-emerald-500/80 bg-emerald-500/10 shadow-lg shadow-emerald-500/10'
                    : 'border-fq-border bg-fq-card/50 hover:border-fq-border-light'
                }`}
              >
                <div className="w-10 h-10 rounded-full bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center text-lg shrink-0">
                  👩‍💼
                </div>
                <div>
                  <div className="font-semibold text-white text-sm">Maya</div>
                  <div className="text-xs text-fq-muted">Encouraging & Insightful</div>
                </div>
              </button>
            </div>
          </div>

          {/* Voice Audio Toggle */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-fq-card/60 border border-fq-border">
            <div>
              <div className="text-sm font-medium text-white">Voice Audio (TTS)</div>
              <div className="text-xs text-fq-muted">
                {muted ? 'Audio is currently muted' : 'Voice will speak mentor dialogue aloud'}
              </div>
            </div>
            <button
              type="button"
              onClick={toggleMute}
              className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                !muted ? 'bg-emerald-500' : 'bg-fq-muted/30'
              }`}
            >
              <span
                className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                  !muted ? 'translate-x-6' : 'translate-x-1'
                }`}
              />
            </button>
          </div>

          {/* Voice Selector */}
          {!muted && voices && voices.length > 0 && (
            <div>
              <label htmlFor="voice-selector" className="block text-xs font-semibold text-fq-muted uppercase tracking-wider mb-2">
                Speech Voice
              </label>
              <select
                id="voice-selector"
                value={preferredVoice || ''}
                onChange={(e) => {
                  setPreferredVoice(e.target.value);
                }}
                className="w-full rounded-xl bg-fq-card border border-fq-border px-3 py-2.5 text-xs text-fq-text focus:outline-none focus:border-cyan-500"
              >
                <option value="">System Best Match ({gender === 'female' ? 'Female' : 'Male'})</option>
                {voices.map((v) => (
                  <option key={v.voiceURI || v.name} value={v.voiceURI || v.name}>
                    {v.name} ({v.lang})
                  </option>
                ))}
              </select>
            </div>
          )}


          {/* Test Voice Button */}
          {!muted && (
            <div className="flex justify-end">
              <button
                type="button"
                onClick={replaySpeech}
                disabled={isSpeaking}
                className="text-xs font-medium px-3 py-1.5 rounded-lg bg-cyan-500/15 text-cyan-400 border border-cyan-500/30 hover:bg-cyan-500/25 transition-colors disabled:opacity-50 flex items-center space-x-1"
              >
                <span>🔊</span>
                <span>{isSpeaking ? 'Speaking...' : 'Test Voice Output'}</span>
              </button>
            </div>
          )}

          {/* Visibility Toggle */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-fq-card/60 border border-fq-border">
            <div>
              <div className="text-sm font-medium text-white">Show Mentor on Screen</div>
              <div className="text-xs text-fq-muted">Toggle floating avatar presence</div>
            </div>
            <button
              type="button"
              onClick={toggleVisibility}
              className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                visible ? 'bg-cyan-500' : 'bg-fq-muted/30'
              }`}
            >
              <span
                className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                  visible ? 'translate-x-6' : 'translate-x-1'
                }`}
              />
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="pt-4 border-t border-fq-border flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-cyan-500 hover:bg-cyan-600 text-white text-xs font-semibold shadow-lg shadow-cyan-500/20 transition-all"
          >
            Done
          </button>
        </div>
      </div>
    </div>
  );
}
