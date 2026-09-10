import React from 'react';

export default function TradingDisclaimer() {
  return (
    <div className="bg-amber-500/10 border border-amber-500/30 rounded-xl p-3 mb-6 flex items-center gap-3">
      <span className="text-xl">⚠️</span>
      <div className="text-xs text-amber-300/90 leading-relaxed">
        <span className="font-bold">Virtual Trading — Educational simulation only.</span> No real money is involved.
        Quotes and prices are simulated for learning purposes. No actual exchange orders are executed.
      </div>
    </div>
  );
}
