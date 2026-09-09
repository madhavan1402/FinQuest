import React, { useState } from 'react';
import { useMentor } from '../../context/MentorContext';
import MentorCanvas from './MentorCanvas';
import MentorDialogue from './MentorDialogue';
import MentorSettingsModal from './MentorSettingsModal';

export default function GlobalMentor() {
  const {
    gender,
    name,
    visible,
    minimized,
    toggleMinimize,
    emotion,
    currentMessage,
    dismissDialogue,
    replaySpeech,
    isSpeaking,
    muted,
    toggleMute,
  } = useMentor();

  const [isSettingsOpen, setIsSettingsOpen] = useState(false);

  if (!visible) return null;

  return (
    <>
      {/* Settings Modal */}
      <MentorSettingsModal
        isOpen={isSettingsOpen}
        onClose={() => setIsSettingsOpen(false)}
      />

      {/* Floating Mentor Container */}
      <aside
        aria-label="3D Finance Mentor Assistant"
        className="fixed bottom-5 right-5 z-[60] flex flex-col items-end pointer-events-none select-none max-w-[calc(100vw-2.5rem)]"
      >
        {/* Dialogue Bubble: Appears above avatar when expanded and not empty */}
        {!minimized && currentMessage && (
          <div className="mb-2 w-full sm:w-80 pointer-events-auto">
            <MentorDialogue
              name={name}
              message={currentMessage}
              isSpeaking={isSpeaking}
              muted={muted}
              onDismiss={dismissDialogue}
              onReplay={replaySpeech}
              onToggleMute={toggleMute}
              onOpenSettings={() => setIsSettingsOpen(true)}
              onMinimize={toggleMinimize}
            />

          </div>
        )}

        {/* Mentor Avatar Controls Area */}
        <div className="flex items-center space-x-2 pointer-events-auto">
          {/* If minimized, show an expand pill button with status */}
          {minimized ? (
            <button
              onClick={toggleMinimize}
              className="flex items-center space-x-2 px-3 py-2 rounded-full bg-fq-surface/90 border border-fq-border shadow-xl backdrop-blur-md hover:border-cyan-500/50 transition-all text-xs font-semibold text-fq-text group cursor-pointer"
              title="Expand Finance Mentor"
              aria-label="Expand Finance Mentor"
            >
              <div className="relative">
                <span className="text-base">{gender === 'female' ? '👩‍💼' : '👨‍💼'}</span>
                {isSpeaking && (
                  <span className="absolute -top-1 -right-1 w-2.5 h-2.5 bg-emerald-400 rounded-full animate-ping" />
                )}
              </div>
              <span className="group-hover:text-cyan-400 transition-colors">
                {name}
              </span>
              <span className="text-xs text-fq-muted">▲</span>
            </button>
          ) : (
            <div className="relative group">
              {/* 3D / Fallback Avatar Canvas Frame */}
              <div
                className="w-24 h-24 sm:w-28 sm:h-28 rounded-2xl bg-fq-surface/80 border border-fq-border shadow-2xl backdrop-blur-md overflow-hidden relative transition-all duration-300 hover:border-cyan-500/50 cursor-pointer"
                onClick={toggleMinimize}
                title="Click to minimize mentor"
              >
                <MentorCanvas gender={gender} emotion={emotion} isSpeaking={isSpeaking} />

                {/* Status bar inside avatar frame */}
                <div className="absolute bottom-0 inset-x-0 bg-black/60 backdrop-blur-xs py-0.5 px-1.5 flex items-center justify-between text-[10px] text-fq-muted">
                  <span className="truncate font-medium text-white/90">{name}</span>
                  <div className="flex items-center space-x-1">
                    {isSpeaking && (
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                    )}
                    <span>{muted ? '🔇' : '🔊'}</span>
                  </div>
                </div>
              </div>

              {/* Quick Action Floating Controls next to Avatar */}
              <div className="absolute -top-2 -left-2 flex flex-col space-y-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    setIsSettingsOpen(true);
                  }}
                  className="w-6 h-6 rounded-full bg-fq-surface border border-fq-border text-white text-[11px] flex items-center justify-center shadow-lg hover:border-cyan-400 hover:bg-fq-card transition-all"
                  title="Mentor Settings"
                  aria-label="Mentor Settings"
                >
                  ⚙️
                </button>
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    toggleMute();
                  }}
                  className="w-6 h-6 rounded-full bg-fq-surface border border-fq-border text-white text-[11px] flex items-center justify-center shadow-lg hover:border-cyan-400 hover:bg-fq-card transition-all"
                  title={muted ? 'Unmute' : 'Mute'}
                  aria-label={muted ? 'Unmute' : 'Mute'}
                >
                  {muted ? '🔇' : '🔊'}
                </button>
              </div>
            </div>
          )}
        </div>
      </aside>
    </>
  );
}
