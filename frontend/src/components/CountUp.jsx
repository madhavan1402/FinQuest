// src/components/CountUp.jsx
// Animates a number from `from` to `to` over `duration` ms.
// Props: from, to, duration (ms), suffix, className
import { useEffect, useRef, useState } from 'react';

export default function CountUp({ from = 0, to, duration = 800, suffix = '', className = '' }) {
  const [display, setDisplay] = useState(from);
  const rafRef = useRef(null);
  const startRef = useRef(null);

  useEffect(() => {
    if (to === undefined || to === null) return;
    const start = from;
    const end = to;
    const diff = end - start;
    if (diff === 0) { setDisplay(end); return; }

    const step = (timestamp) => {
      if (!startRef.current) startRef.current = timestamp;
      const elapsed = timestamp - startRef.current;
      const progress = Math.min(elapsed / duration, 1);
      // Ease out cubic
      const eased = 1 - Math.pow(1 - progress, 3);
      setDisplay(Math.round(start + diff * eased));
      if (progress < 1) {
        rafRef.current = requestAnimationFrame(step);
      } else {
        setDisplay(end);
      }
    };

    startRef.current = null;
    rafRef.current = requestAnimationFrame(step);
    return () => { if (rafRef.current) cancelAnimationFrame(rafRef.current); };
  }, [to, from, duration]);

  return <span className={className}>{display}{suffix}</span>;
}
