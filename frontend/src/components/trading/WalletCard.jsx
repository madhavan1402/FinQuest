import React from 'react';

export default function WalletCard({ wallet }) {
  const cash = wallet?.cashBalance ?? 0;
  const initial = wallet?.initialBalance ?? 100000;

  return (
    <div className="bg-slate-850 border border-slate-750 rounded-2xl p-5 shadow-lg relative overflow-hidden">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-2xl">💰</span>
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Virtual Cash Wallet</span>
        </div>
        <span className="text-[10px] font-semibold tracking-wider uppercase px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
          Simulated
        </span>
      </div>

      <div className="text-3xl font-black text-white tracking-tight">
        ₹{Number(cash).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
      </div>

      <div className="flex items-center justify-between text-xs text-slate-400 mt-3 pt-3 border-t border-slate-750/80">
        <span>Starting Balance:</span>
        <span className="font-semibold text-slate-300">
          ₹{Number(initial).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </span>
      </div>
    </div>
  );
}
