// src/components/Modal.jsx
// Reusable animated modal — backdrop fade + content scale.
// Props: open, onClose, children, maxWidth (default 'max-w-sm')
import { useEffect } from 'react';

export default function Modal({ open, onClose, children, maxWidth = 'max-w-sm' }) {
  // Close on ESC
  useEffect(() => {
    if (!open) return;
    const handler = (e) => { if (e.key === 'Escape') onClose?.(); };
    document.addEventListener('keydown', handler);
    return () => document.removeEventListener('keydown', handler);
  }, [open, onClose]);

  // Prevent body scroll
  useEffect(() => {
    document.body.style.overflow = open ? 'hidden' : '';
    return () => { document.body.style.overflow = ''; };
  }, [open]);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-[9990] flex items-center justify-center px-4"
      style={{ animation: 'fadeIn 0.2s ease' }}
      role="dialog"
      aria-modal="true"
    >
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/70 backdrop-blur-sm"
        onClick={onClose}
        aria-hidden="true"
      />
      {/* Content */}
      <div
        className={`relative z-10 w-full ${maxWidth}`}
        style={{ animation: 'scaleIn 0.25s cubic-bezier(0.34,1.56,0.64,1) forwards' }}
      >
        {children}
      </div>
    </div>
  );
}
