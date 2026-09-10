import React from 'react';

export default function TransactionHistory({ transactions }) {
  if (!transactions || transactions.length === 0) {
    return (
      <div className="bg-slate-850 border border-slate-750 rounded-2xl p-6 text-center shadow-lg">
        <span className="text-3xl">📜</span>
        <h4 className="text-base font-bold text-white mt-2">No Transactions</h4>
        <p className="text-xs text-slate-400 mt-1">
          Your executed simulated trades will be recorded here.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-slate-850 border border-slate-750 rounded-2xl p-5 shadow-lg overflow-hidden">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-xl">📜</span>
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Trade Audit Ledger</span>
        </div>
        <span className="text-xs text-slate-400 font-medium">{transactions.length} recorded</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs text-slate-300">
          <thead className="bg-slate-900/60 text-slate-400 uppercase font-semibold text-[10px] tracking-wider border-b border-slate-750">
            <tr>
              <th className="py-2.5 px-3">Type</th>
              <th className="py-2.5 px-3">Symbol</th>
              <th className="py-2.5 px-3">Qty</th>
              <th className="py-2.5 px-3">Execution Price</th>
              <th className="py-2.5 px-3">Total Value</th>
              <th className="py-2.5 px-3">Realized P/L</th>
              <th className="py-2.5 px-3 text-right">Timestamp</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-750/50">
            {transactions.map((tx) => {
              const isBuy = tx.transactionType === 'BUY';
              const hasPnl = tx.realizedProfitLoss !== null && tx.realizedProfitLoss !== undefined;
              const isPnlPositive = Number(tx.realizedProfitLoss) >= 0;

              return (
                <tr key={tx.id} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-2.5 px-3">
                    <span
                      className={`font-bold px-2 py-0.5 rounded text-[10px] uppercase ${
                        isBuy
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                          : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                      }`}
                    >
                      {tx.transactionType}
                    </span>
                  </td>
                  <td className="py-2.5 px-3 font-bold text-white">{tx.symbol}</td>
                  <td className="py-2.5 px-3">{tx.quantity}</td>
                  <td className="py-2.5 px-3">₹{Number(tx.executionPrice).toFixed(2)}</td>
                  <td className="py-2.5 px-3 font-semibold text-slate-200">
                    ₹{Number(tx.totalValue).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </td>
                  <td className="py-2.5 px-3">
                    {hasPnl ? (
                      <span className={`font-semibold ${isPnlPositive ? 'text-emerald-400' : 'text-rose-400'}`}>
                        {isPnlPositive ? '+' : ''}₹{Number(tx.realizedProfitLoss).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                      </span>
                    ) : (
                      <span className="text-slate-500">—</span>
                    )}
                  </td>
                  <td className="py-2.5 px-3 text-right text-slate-400 text-[11px]">
                    {new Date(tx.timestamp).toLocaleString()}
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
