// src/context/MentorContext.jsx
// Global state and event dispatcher for the 3D Finance Mentor.
// Persists user preferences (gender, voice, mute, minimized) in localStorage,
// coordinates TTS audio with avatar animation, and exposes high-level mentor events.
// Phase 5: Uses AiFinanceBrainProvider with silent deterministic fallback,
// race condition / request versioning protection, and isThinking avatar state.

import { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import { useMentorTTS } from '../hooks/useMentorTTS';
import { aiFinanceBrainProvider } from '../services/AiFinanceBrainProvider';
import { mentorProvider as deterministicFallback } from '../services/mentorProvider';

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
  const [isThinking, setIsThinking] = useState(false);

  const previousEmotionRef = useRef('idle');
  // Race condition protection: monotonically increasing request version ID
  const latestRequestIdRef = useRef(0);

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

  /**
   * Dispatches an asynchronous request to the AI Finance Brain provider.
   * Manages `isThinking` avatar state, checks monotonic request IDs to prevent race conditions,
   * and triggers speech/dialogue execution once resolved.
   */
  const dispatchAiRequest = useCallback(async (asyncFn, syncFallbackFn, targetEmotion = 'idle') => {
    const requestId = ++latestRequestIdRef.current;
    setIsThinking(true);
    setEmotion('thinking');

    try {
      const result = await asyncFn();
      // Drop response if a newer request has started
      if (requestId !== latestRequestIdRef.current) return;

      setIsThinking(false);
      if (result && result.message) {
        executeDialogue(result.message, result.emotion || targetEmotion);
      } else if (syncFallbackFn) {
        const fb = syncFallbackFn();
        executeDialogue(fb.message, fb.emotion || targetEmotion);
      }
    } catch {
      if (requestId !== latestRequestIdRef.current) return;
      setIsThinking(false);
      if (syncFallbackFn) {
        const fb = syncFallbackFn();
        executeDialogue(fb.message, fb.emotion || targetEmotion);
      }
    }
  }, [executeDialogue]);

  // ── High-Level Mentor Events (AI-Powered with Fallback) ───────────────────

  const greet = useCallback((userName) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getGreeting(userName),
      () => deterministicFallback.getGreeting(userName),
      'happy'
    );
  }, [dispatchAiRequest]);

  const say = useCallback((message, emo = 'talking', autoSpeak = true) => {
    // Immediate direct utterance without network call
    latestRequestIdRef.current++;
    setIsThinking(false);
    executeDialogue(message, emo, autoSpeak);
  }, [executeDialogue]);

  const react = useCallback((emo, customMessage) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getReaction(emo, customMessage),
      () => deterministicFallback.getReaction(emo, customMessage),
      emo || 'talking'
    );
  }, [dispatchAiRequest]);

  const celebrate = useCallback((rewardTitle, xp) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getCelebration(rewardTitle, xp),
      () => deterministicFallback.getCelebration(rewardTitle, xp),
      'celebrating'
    );
  }, [dispatchAiRequest]);

  const encourage = useCallback((levelTitle) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getEncouragement(levelTitle),
      () => deterministicFallback.getEncouragement(levelTitle),
      'encouraging'
    );
  }, [dispatchAiRequest]);

  const think = useCallback((topic) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getThinking(topic),
      () => deterministicFallback.getThinking(topic),
      'thinking'
    );
  }, [dispatchAiRequest]);

  const explain = useCallback((tip) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getFinancialTip(tip),
      () => deterministicFallback.getFinancialTip(tip),
      'talking'
    );
  }, [dispatchAiRequest]);

  const recommendNext = useCallback((levelNumber, title, reason) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.getRecommendation(levelNumber, title, reason),
      () => deterministicFallback.getRecommendation(levelNumber, title, reason),
      'encouraging'
    );
  }, [dispatchAiRequest]);

  const chat = useCallback((userQuery, pageContext) => {
    dispatchAiRequest(
      () => aiFinanceBrainProvider.chat(userQuery, pageContext),
      () => ({
        message: 'Every thoughtful financial decision moves you closer to long-term wealth building.',
        emotion: 'talking',
      }),
      'talking'
    );
  }, [dispatchAiRequest]);

  const replaySpeech = useCallback(() => {
    if (currentMessage) {
      executeDialogue(currentMessage, emotion, true);
    }
  }, [currentMessage, emotion, executeDialogue]);

  const dismissMessage = useCallback(() => {
    ttsCancel();
    setIsSpeaking(false);
    setIsThinking(false);
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
    isThinking,

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
    chat,
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
