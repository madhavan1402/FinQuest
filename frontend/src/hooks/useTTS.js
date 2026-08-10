// src/hooks/useTTS.js
import { useCallback } from 'react';

export function useTTS() {
  const speak = useCallback((text, onStart, onEnd) => {
    if (!window.speechSynthesis) return;
    window.speechSynthesis.cancel();

    const utter = new SpeechSynthesisUtterance(text);

    const trySpeak = () => {
      const voices = window.speechSynthesis.getVoices();
      const preferred =
        voices.find(v => v.name.includes('Microsoft') && v.name.includes('Neural')) ||
        voices.find(v => v.name.includes('Microsoft')) ||
        voices.find(v => v.lang.startsWith('en'));
      if (preferred) utter.voice = preferred;
      utter.rate = 1.0;
      utter.pitch = 1.1;
      utter.volume = 1;
      utter.onstart = () => onStart?.();
      utter.onend = () => onEnd?.();
      utter.onerror = () => onEnd?.();
      window.speechSynthesis.speak(utter);
    };

    if (window.speechSynthesis.getVoices().length === 0) {
      window.speechSynthesis.onvoiceschanged = trySpeak;
    } else {
      trySpeak();
    }
  }, []);

  const cancel = useCallback(() => window.speechSynthesis?.cancel(), []);

  return { speak, cancel };
}
