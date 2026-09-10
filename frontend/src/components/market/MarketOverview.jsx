// src/components/market/MarketOverview.jsx
import React, { useState, useEffect } from 'react';
import { getMarketOverview } from '../../api/endpoints';
import MarketStatusBadge from './MarketStatusBadge';
import MarketChart from './MarketChart';

export default function MarketOverview() {
  const [data, setData] = useState(null);
  const [selectedSymbol, setSelectedSymbol] = useState('NIFTY50');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchOverview = () => {
    getMarketOverview()
      .then(res => {
        setData(res.data);
        setError('');
      })
      .catch(() => {
        setError('Market data currently unavailable.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchOverview();
    const timer = setInterval(fetchOverview, 30000); // 30-sec polling
    return () => clearInterval(timer);
  }, []);

  if (loading && !data) {
    return (
      <div className="bg-slate-800/80 border border-slate-700/60 rounded-2xl p-6 mb-8 text-center">
        <div className="flex items-center justify-center gap-2 text-slate-400 text-sm">
          <span className="w-4 h-4 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin" />
          Loading market infrastructure…
        </div>
      </div>
    );
  }

  if (error && !data) {
    return (
      <div className="bg-slate-800/80 border border-slate-700/60 rounded-2xl p-6 mb-8 flex items-center justify-between">
        <span className="text-sm text-slate-400">📊 Market data feed idle</span>
        <button onClick={fetchOverview} className="text-xs text-indigo-400 hover:underline">
          Retry
        </button>
      </div>
    );
  }

  const benchmarks = data?.benchmarks || [];
  const watchlist = data?.watchlist || [];
  const status = data?.marketStatus?.status;
  const isSimulated = data?.isSimulated ?? true;

  const activeQuote = [...benchmarks, ...watchlist].find(q => q.symbol === selectedSymbol) || benchmarks[0];

  return (
    <div className="bg-slate-800/85 border border-slate-700/70 rounded-2xl p-6 mb-8 shadow-xl backdrop-blur-sm">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 pb-4 border-b border-slate-700/60">
        <div className="flex items-center gap-3">
          <span className="text-2xl">📈</span>
          <div>
            <h2 className="text-xl font-bold text-white tracking-tight">Market Intelligence</h2>
            <p className="text-xs text-slate-400">Indian Benchmark & Equity Watchlist</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <MarketStatusBadge status={status} isSimulated={isSimulated} />
          <button
            onClick={fetchOverview}
            title="Refresh Feed"
            className="p-1.5 text-slate-400 hover:text-white rounded-lg bg-slate-700/40 hover:bg-slate-700 border border-slate-600/40 transition-colors text-xs"
          >
            🔄
          </button>
        </div>
      </div>

      {/* Benchmarks Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
        {benchmarks.map((quote) => {
          const isUp = quote.change >= 0;
          const isSelected = quote.symbol === selectedSymbol;
          return (
            <div
              key={quote.symbol}
              onClick={() => setSelectedSymbol(quote.symbol)}
              className={`p-4 rounded-xl cursor-pointer border transition-all duration-200 ${
                isSelected
                  ? 'bg-slate-900/90 border-indigo-500/80 shadow-lg shadow-indigo-500/10'
                  : 'bg-slate-900/40 border-slate-700/50 hover:border-slate-600 hover:bg-slate-900/60'
              }`}
            >
              <div className="flex justify-between items-start mb-2">
                <div>
                  <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                    {quote.exchange}
                  </span>
                  <h3 className="text-base font-bold text-white">{quote.displayName}</h3>
                </div>
                <span
                  className={`text-xs font-bold px-2 py-0.5 rounded ${
                    isUp ? 'bg-emerald-500/15 text-emerald-400' : 'bg-rose-500/15 text-rose-400'
                  }`}
                >
                  {isUp ? '▲' : '▼'} {Math.abs(quote.changePercent).toFixed(2)}%
                </span>
              </div>
              <div className="flex items-baseline gap-2">
                <span className="text-xl font-extrabold text-white">
                  ₹{quote.currentPrice.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                </span>
                <span className={`text-xs font-medium ${isUp ? 'text-emerald-400' : 'text-rose-400'}`}>
                  {isUp ? '+' : ''}{quote.change.toFixed(2)}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Interactive Chart Component */}
      {activeQuote && (
        <div className="mb-6">
          <MarketChart symbol={activeQuote.symbol} displayName={activeQuote.displayName} />
        </div>
      )}

      {/* Equity Watchlist Bar */}
      <div>
        <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
          Top Equities Watchlist
        </h4>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3">
          {watchlist.map((stock) => {
            const isUp = stock.change >= 0;
            const isSelected = stock.symbol === selectedSymbol;
            return (
              <div
                key={stock.symbol}
                onClick={() => setSelectedSymbol(stock.symbol)}
                className={`p-3 rounded-lg border text-left cursor-pointer transition-colors ${
                  isSelected
                    ? 'bg-slate-900 border-indigo-500'
                    : 'bg-slate-900/30 border-slate-700/40 hover:bg-slate-900/60 hover:border-slate-600'
                }`}
              >
                <div className="text-xs font-bold text-white truncate">{stock.symbol}</div>
                <div className="text-[11px] text-slate-400 truncate mb-1.5">{stock.displayName}</div>
                <div className="text-sm font-extrabold text-white">
                  ₹{stock.currentPrice.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                </div>
                <div className={`text-[10px] font-semibold ${isUp ? 'text-emerald-400' : 'text-rose-400'}`}>
                  {isUp ? '+' : ''}{stock.changePercent.toFixed(2)}%
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
