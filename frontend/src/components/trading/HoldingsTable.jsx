import React from 'react';

export default function HoldingsTable({ holdings, onSelectStock }) {
  if (!holdings || holdings.length === 0) {
    return (
      <div className="bg-slate-850 border border-slate-750 rounded-2xl p-6 text-center shadow-lg">
        <span className="text-3xl">📦</span>
        <h4 className="text-base font-bold text-white mt-2">No Holdings Yet</h4>
        <p className="text-xs text-slate-400 mt-1">
          Use the order panel to buy shares with your virtual cash.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-slate-850 border border-slate-750 rounded-2xl p-5 shadow-lg overflow-hidden">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-xl">💼</span>
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Current Holdings</span>
        </div>
        <span className="text-xs text-slate-400 font-medium">{holdings.length} {holdings.length === 1 ? 'stock' : 'stocks'}</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs text-slate-300">
          <thead className="bg-slate-900/60 text-slate-400 uppercase font-semibold text-[10px] tracking-wider border-b border-slate-750">
            <tr>
              <th className="py-2.5 px-3">Symbol</th>
              <th className="py-2.5 px-3">Qty</th>
              <th className="py-2.5 px-3">Avg Buy</th>
              <th className="py-2.5 px-3">Cur Price</th>
              <th className="py-2.5 px-3">Market Value</th>
              <th className="py-2.5 px-3">Unrealized P/L</th>
              <th className="py-2.5 px-3 text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-750/50">
            {holdings.map((h) => {
              const isProfit = Number(h.unrealizedPnl) >= 0;
              return (
                <tr key={h.symbol} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 px-3 font-bold text-white">{h.symbol}</td>
                  <td className="py-3 px-3">{h.quantity}</td>
                  <td className="py-3 px-3">₹{Number(h.averageBuyPrice).toFixed(2)}</td>
                  <td className="py-3 px-3">₹{Number(h.currentPrice).toFixed(2)}</td>
                  <td className="py-3 px-3 font-semibold text-slate-200">
                    ₹{Number(h.marketValue).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </td>
                  <td className={`py-3 px-3 font-semibold ${isProfit ? 'text-emerald-400' : 'text-rose-400'}`}>
                    {isProfit ? '+' : ''}₹{Number(h.unrealizedPnl).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    <span className="text-[10px] ml-1 opacity-80">
                      ({isProfit ? '+' : ''}{Number(h.unrealizedPnlPercent).toFixed(2)}%)
                    </span>
                  </td>
                  <td className="py-3 px-3 text-right">
                    <button
                      onClick={() => onSelectStock && onSelectStock(h.symbol)}
                      className="px-2.5 py-1 text-[11px] font-bold rounded bg-indigo-600/20 text-indigo-400 hover:bg-indigo-600/30 border border-indigo-500/30 transition-colors"
                    >
                      Trade
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
