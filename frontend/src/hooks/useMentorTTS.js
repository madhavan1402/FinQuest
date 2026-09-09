// src/hooks/useMentorTTS.js
// Production-grade Browser SpeechSynthesis controller for Finance Mentor.
// Handles safe voice loading, voiceschanged event, best-effort male/female matching,
// speech lifecycle callbacks, and graceful degradation if audio is blocked or unavailable.

import { useState, useEffect, useCallback, useRef } from 'react';

const MALE_NAMES = ['david', 'mark', 'guy', 'george', 'james', 'alex', 'brian', 'male', 'daniel', 'richard', 'tom'];
const FEMALE_NAMES = ['zira', 'samantha', 'victoria', 'karen', 'female', 'jenny', 'aria', 'sara', 'hazel', 'catherine', 'susan', 'linda'];

export function useMentorTTS() {
  const [voices, setVoices] = useState([]);
  const [supported] = useState(() => typeof window !== 'undefined' && 'speechSynthesis' in window);
  const activeUtteranceRef = useRef(null);

  useEffect(() => {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      const updateVoices = () => {
        try {
          const available = window.speechSynthesis.getVoices() || [];
          setVoices(available);
        } catch {
          setVoices([]);
        }
      };

      updateVoices();
      if (window.speechSynthesis.onvoiceschanged !== undefined) {
        window.speechSynthesis.onvoiceschanged = updateVoices;
      }
    }
  }, []);


  /**
   * Find the best voice for the given gender and language preferences.
   */
  const getBestVoice = useCallback((gender, preferredVoiceUri) => {
    if (!voices.length) return null;

    // 1. Exact URI match if user explicitly chose a voice
    if (preferredVoiceUri) {
      const matched = voices.find(v => v.voiceURI === preferredVoiceUri || v.name === preferredVoiceUri);
      if (matched) return matched;
    }

    const englishVoices = voices.filter(v => (v.lang || '').toLowerCase().startsWith('en'));
    const candidateList = englishVoices.length ? englishVoices : voices;

    const isFemale = gender === 'female';
    const targetKeywords = isFemale ? FEMALE_NAMES : MALE_NAMES;

    // 2. High-quality neural / natural voice matching requested gender
    const neuralMatch = candidateList.find(v => {
      const name = (v.name || '').toLowerCase();
      const isNeural = name.includes('natural') || name.includes('neural') || name.includes('online');
      return isNeural && targetKeywords.some(k => name.includes(k));
    });
    if (neuralMatch) return neuralMatch;

    // 3. Any voice matching requested gender keywords
    const genderMatch = candidateList.find(v => {
      const name = (v.name || '').toLowerCase();
      return targetKeywords.some(k => name.includes(k));
    });
    if (genderMatch) return genderMatch;

    // 4. Any English voice
    if (englishVoices.length) return englishVoices[0];

    // 5. Fallback to system default
    return voices.find(v => v.default) || voices[0];
  }, [voices]);

  /**
   * Cancel ongoing speech.
   */
  const cancel = useCallback(() => {
    try {
      if (typeof window !== 'undefined' && window.speechSynthesis) {
        window.speechSynthesis.cancel();
      }
    } catch {
      // Graceful ignore
    }
    activeUtteranceRef.current = null;
  }, []);

  /**
   * Speak the given text with gender-matched voice and lifecycle events.
   */
  const speak = useCallback((text, gender = 'male', options = {}) => {
    if (!supported || !text || options.muted) {
      options.onEnd?.();
      return;
    }

    try {
      window.speechSynthesis.cancel();

      const utter = new SpeechSynthesisUtterance(text);
      activeUtteranceRef.current = utter;

      const voice = getBestVoice(gender, options.preferredVoice);
      if (voice) utter.voice = voice;

      utter.rate = options.rate ?? 1.0;
      utter.pitch = options.pitch ?? (gender === 'female' ? 1.15 : 0.95);
      utter.volume = options.volume ?? 1.0;

      utter.onstart = () => {
        options.onStart?.();
      };

      utter.onend = () => {
        activeUtteranceRef.current = null;
        options.onEnd?.();
      };

      utter.onerror = () => {
        activeUtteranceRef.current = null;
        options.onEnd?.();
      };


      window.speechSynthesis.speak(utter);
    } catch {
      options.onEnd?.();
    }
  }, [supported, getBestVoice]);

  return {
    speak,
    cancel,
    voices,
    supported,
    getBestVoice,
  };
}
