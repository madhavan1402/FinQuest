// src/context/MentorContext.jsx
// Global state and event dispatcher for the 3D Finance Mentor.
// Persists user preferences (gender, voice, mute, minimized) in localStorage,
// coordinates TTS audio with avatar animation, and exposes high-level mentor events.

import { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import { useMentorTTS } from '../hooks/useMentorTTS';
import { mentorProvider } from '../services/mentorProvider';

const MENTOR_SETTINGS_KEY = 'fq_mentor_settings';
const VOICE_NAVBAR_KEY = 'fq_voice_enabled';

const DEFAULT_SETTINGS = {
  gender: 'male',
  muted: false,
  minimized: false,
  preferredVoice: '',
};

const MentorContext = createContext(null);

export function MentorProvider({ children }) {
  // Load saved preferences
  const [settings, setSettings] = useState(() => {
    const isSmallScreen = typeof window !== 'undefined' && window.innerWidth < 768;
    try {
      const stored = localStorage.getItem(MENTOR_SETTINGS_KEY);
      const navVoice = localStorage.getItem(VOICE_NAVBAR_KEY);
      const base = stored ? JSON.parse(stored) : { ...DEFAULT_SETTINGS, minimized: isSmallScreen };
      if (navVoice !== null) {
        base.muted = navVoice === 'false';
      }
      return { ...DEFAULT_SETTINGS, minimized: isSmallScreen, ...base };
    } catch {
      return { ...DEFAULT_SETTINGS, minimized: isSmallScreen };
    }
  });


  const gender = settings.gender || 'male';
  const mentorName = gender === 'female' ? 'Maya' : 'Alex';
  const muted = Boolean(settings.muted);
  const minimized = Boolean(settings.minimized);
  const preferredVoice = settings.preferredVoice || '';

  // Avatar and dialogue state
  const [visible, setVisible] = useState(true);
  const [emotion, setEmotion] = useState('idle');
  const [currentMessage, setCurrentMessage] = useState('');
  const [isSpeaking, setIsSpeaking] = useState(false);
  const previousEmotionRef = useRef('idle');

  // TTS Hook
  const { speak: ttsSpeak, cancel: ttsCancel, voices, supported: ttsSupported } = useMentorTTS();

  // Save settings helper
  const updateSettings = useCallback((newPartial) => {
    setSettings(prev => {
      const updated = { ...prev, ...newPartial };
      try {
        localStorage.setItem(MENTOR_SETTINGS_KEY, JSON.stringify(updated));
        if (newPartial.muted !== undefined) {
          localStorage.setItem(VOICE_NAVBAR_KEY, String(!newPartial.muted));
        }
      } catch {
        // Ignore storage quotas
      }
      return updated;
    });
  }, []);

  // Listen to external navbar changes
  useEffect(() => {
    const handleStorage = (e) => {
      if (e.key === VOICE_NAVBAR_KEY) {
        const isMuted = e.newValue === 'false';
        setSettings(s => ({ ...s, muted: isMuted }));
        if (isMuted) ttsCancel();
      }
    };
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, [ttsCancel]);

  /**
   * Internal speech and dialogue executor.
   */

  const executeDialogue = useCallback((message, targetEmotion = 'idle', autoSpeak = true) => {
    if (!message) return;
    setCurrentMessage(message);
    const baseEmotion = targetEmotion || 'idle';
    previousEmotionRef.current = baseEmotion;

    if (autoSpeak && !muted && ttsSupported) {
      setEmotion('talking');
      setIsSpeaking(true);

      ttsSpeak(message, gender, {
        muted,
        preferredVoice,
        onStart: () => {
          setIsSpeaking(true);
          setEmotion('talking');
        },
        onEnd: () => {
          setIsSpeaking(false);
          setEmotion(previousEmotionRef.current);
        },
      });
    } else {
      setEmotion(baseEmotion);
      setIsSpeaking(false);
    }
  }, [muted, ttsSupported, ttsSpeak, gender, preferredVoice]);

  // ── High-Level Mentor Events ──────────────────────────────────────────────

  const greet = useCallback((userName) => {
    const { message, emotion: emo } = mentorProvider.getGreeting(userName);
    executeDialogue(message, emo);
  }, [executeDialogue]);

  const say = useCallback((message, emo = 'talking', autoSpeak = true) => {
    executeDialogue(message, emo, autoSpeak);
  }, [executeDialogue]);

  const react = useCallback((emo, customMessage) => {
    const { message, emotion: resultEmo } = mentorProvider.getReaction(emo, customMessage);
    executeDialogue(message, resultEmo);
  }, [executeDialogue]);

  const celebrate = useCallback((rewardTitle, xp) => {
    const { message, emotion: emo } = mentorProvider.getCelebration(rewardTitle, xp);
    executeDialogue(message, emo);
  }, [executeDialogue]);

  const encourage = useCallback((levelTitle) => {
    const { message, emotion: emo } = mentorProvider.getEncouragement(levelTitle);
    executeDialogue(message, emo);
  }, [executeDialogue]);

  const think = useCallback((topic) => {
    const { message, emotion: emo } = mentorProvider.getThinking(topic);
    executeDialogue(message, emo);
  }, [executeDialogue]);

  const explain = useCallback((tip) => {
    const { message, emotion: emo } = mentorProvider.getFinancialTip(tip);
    executeDialogue(message, emo);
  }, [executeDialogue]);

  const recommendNext = useCallback((levelNumber, title, reason) => {
    const { message, emotion: emo } = mentorProvider.getRecommendation(levelNumber, title, reason);
    executeDialogue(message, emo);
  }, [executeDialogue]);

  const replaySpeech = useCallback(() => {
    if (currentMessage) {
      executeDialogue(currentMessage, emotion, true);
    }
  }, [currentMessage, emotion, executeDialogue]);

  const dismissMessage = useCallback(() => {
    ttsCancel();
    setIsSpeaking(false);
    setCurrentMessage('');
    setEmotion('idle');
  }, [ttsCancel]);

  const toggleMute = useCallback(() => {
    const nextMuted = !muted;
    if (nextMuted) ttsCancel();
    updateSettings({ muted: nextMuted });
  }, [muted, ttsCancel, updateSettings]);

  const toggleMinimize = useCallback(() => {
    updateSettings({ minimized: !minimized });
  }, [minimized, updateSettings]);

  const setGender = useCallback((newGender) => {
    if (newGender === 'male' || newGender === 'female') {
      ttsCancel();
      updateSettings({ gender: newGender });
    }
  }, [ttsCancel, updateSettings]);

  const setPreferredVoice = useCallback((voiceUri) => {
    updateSettings({ preferredVoice: voiceUri });
  }, [updateSettings]);

  const value = {
    // Identity & Preferences
    gender,
    name: mentorName,
    visible,
    minimized,
    muted,
    preferredVoice,
    voices,
    ttsSupported,

    // Dialogue & Animation State
    emotion,
    currentMessage,
    isSpeaking,

    // Controls
    setVisible,
    setGender,
    setPreferredVoice,
    toggleMute,
    toggleMinimize,
    dismissMessage,
    dismissDialogue: dismissMessage,
    replaySpeech,


    // High-level event triggers
    greet,
    say,
    react,
    celebrate,
    encourage,
    think,
    explain,
    recommendNext,
  };

  return (
    <MentorContext.Provider value={value}>
      {children}
    </MentorContext.Provider>
  );
}

export const useMentor = () => {
  const ctx = useContext(MentorContext);
  if (!ctx) {
    throw new Error('useMentor must be used within a MentorProvider');
  }
  return ctx;
};
