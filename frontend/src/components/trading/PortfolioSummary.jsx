import React from 'react';

export default function PortfolioSummary({ portfolio }) {
  const portfolioVal = portfolio?.portfolioValue ?? 100000;
  const invested = portfolio?.totalCostBasis ?? 0;
  const unrealized = portfolio?.totalUnrealizedPnl ?? 0;
  const realized = portfolio?.realizedPnl ?? 0;
  const totalPnl = portfolio?.totalPnl ?? 0;

  const isPositive = Number(totalPnl) >= 0;

  return (
    <div className="bg-slate-850 border border-slate-750 rounded-2xl p-5 shadow-lg">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <span className="text-2xl">📊</span>
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Portfolio Overview</span>
        </div>
        <div className={`text-xs font-bold px-2.5 py-1 rounded-lg ${isPositive ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'}`}>
          Total P/L: {isPositive ? '+' : ''}₹{Number(totalPnl).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </div>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800">
          <div className="text-[11px] text-slate-400 uppercase font-semibold">Portfolio Value</div>
          <div className="text-lg font-black text-white mt-0.5">
            ₹{Number(portfolioVal).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800">
          <div className="text-[11px] text-slate-400 uppercase font-semibold">Invested Amount</div>
          <div className="text-lg font-black text-slate-200 mt-0.5">
            ₹{Number(invested).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800">
          <div className="text-[11px] text-slate-400 uppercase font-semibold">Unrealized P/L</div>
          <div className={`text-lg font-black mt-0.5 ${Number(unrealized) >= 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
            {Number(unrealized) >= 0 ? '+' : ''}₹{Number(unrealized).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>

        <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800">
          <div className="text-[11px] text-slate-400 uppercase font-semibold">Realized P/L</div>
          <div className={`text-lg font-black mt-0.5 ${Number(realized) >= 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
            {Number(realized) >= 0 ? '+' : ''}₹{Number(realized).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
          </div>
        </div>
      </div>
    </div>
  );
}
