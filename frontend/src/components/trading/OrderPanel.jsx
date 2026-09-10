import React, { useState, useEffect } from 'react';
import { executeTradingBuy, executeTradingSell, getMarketQuote } from '../../api/endpoints';

const TRADEABLE_SYMBOLS = ['RELIANCE', 'TCS', 'HDFCBANK', 'INFY', 'ICICIBANK'];

export default function OrderPanel({ selectedSymbol, onTradeSuccess, walletCash, holdings }) {
  const [action, setAction] = useState('BUY'); // 'BUY' or 'SELL'
  const [symbol, setSymbol] = useState(selectedSymbol || 'RELIANCE');
  const [quantity, setQuantity] = useState(1);
  const [quote, setQuote] = useState(null);
  const [loadingQuote, setLoadingQuote] = useState(false);
  const [executing, setExecuting] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    if (selectedSymbol) {
      setSymbol(selectedSymbol);
    }
  }, [selectedSymbol]);

  useEffect(() => {
    let isMounted = true;
    setLoadingQuote(true);
    setError('');
    getMarketQuote(symbol)
      .then((res) => {
        if (isMounted) setQuote(res.data);
      })
      .catch(() => {
        if (isMounted) setError('Price feed temporarily unavailable for ' + symbol);
      })
      .finally(() => {
        if (isMounted) setLoadingQuote(false);
      });
    return () => {
      isMounted = false;
    };
  }, [symbol]);

  const currentPrice = quote?.currentPrice || 0;
  const estimatedTotal = (Number(quantity) || 0) * currentPrice;
  const ownedHolding = holdings?.find((h) => h.symbol === symbol);
  const ownedQty = ownedHolding?.quantity || 0;

  const handleTrade = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');

    const qty = parseInt(quantity, 10);
    if (!qty || qty <= 0) {
      setError('Please enter a valid quantity greater than 0.');
      return;
    }

    if (action === 'BUY' && estimatedTotal > (walletCash || 0)) {
      setError(`Insufficient virtual cash! Required ₹${estimatedTotal.toFixed(2)}, Available ₹${(walletCash || 0).toFixed(2)}`);
      return;
    }

    if (action === 'SELL' && qty > ownedQty) {
      setError(`Cannot sell ${qty} shares! You only own ${ownedQty} shares of ${symbol}.`);
      return;
    }

    setExecuting(true);
    try {
      const payload = { symbol, quantity: qty };
      let res;
      if (action === 'BUY') {
        res = await executeTradingBuy(payload);
        setSuccessMsg(`✅ Successfully bought ${qty} ${symbol} @ ₹${Number(res.data.executionPrice).toFixed(2)}`);
      } else {
        res = await executeTradingSell(payload);
        setSuccessMsg(`✅ Successfully sold ${qty} ${symbol} @ ₹${Number(res.data.executionPrice).toFixed(2)} (P/L: ₹${Number(res.data.realizedProfitLoss).toFixed(2)})`);
      }
      setQuantity(1);
      if (onTradeSuccess) onTradeSuccess();
    } catch (err) {
      const msg = err.response?.data?.message || 'Trade execution failed. Please check parameters.';
      setError(msg);
    } finally {
      setExecuting(false);
    }
  };

  return (
    <div className="bg-slate-850 border border-slate-750 rounded-2xl p-5 shadow-lg">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <span className="text-xl">⚡</span>
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Order Execution</span>
        </div>
        <div className="flex bg-slate-900/80 p-0.5 rounded-lg border border-slate-800">
          <button
            type="button"
            onClick={() => { setAction('BUY'); setError(''); setSuccessMsg(''); }}
            className={`px-3 py-1 text-xs font-bold rounded-md transition-all ${
              action === 'BUY'
                ? 'bg-emerald-600 text-white shadow-md'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            BUY
          </button>
          <button
            type="button"
            onClick={() => { setAction('SELL'); setError(''); setSuccessMsg(''); }}
            className={`px-3 py-1 text-xs font-bold rounded-md transition-all ${
              action === 'SELL'
                ? 'bg-rose-600 text-white shadow-md'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            SELL
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs p-2.5 rounded-xl mb-3">
          {error}
        </div>
      )}

      {successMsg && (
        <div className="bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs p-2.5 rounded-xl mb-3">
          {successMsg}
        </div>
      )}

      <form onSubmit={handleTrade} className="space-y-4">
        <div>
          <label className="block text-[11px] font-semibold text-slate-400 uppercase tracking-wider mb-1">
            Tradeable Stock
          </label>
          <select
            value={symbol}
            onChange={(e) => setSymbol(e.target.value)}
            className="w-full bg-slate-900 border border-slate-750 rounded-xl px-3 py-2 text-white font-medium text-xs focus:outline-none focus:border-indigo-500"
          >
            {TRADEABLE_SYMBOLS.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
        </div>

        <div className="flex items-center justify-between text-xs px-1">
          <span className="text-slate-400">Current Market Price:</span>
          <span className="font-bold text-white">
            {loadingQuote ? 'Fetching...' : `₹${currentPrice.toFixed(2)}`}
          </span>
        </div>

        {action === 'SELL' && (
          <div className="flex items-center justify-between text-xs px-1 text-slate-400">
            <span>Available to Sell:</span>
            <span className="font-bold text-slate-200">{ownedQty} shares</span>
          </div>
        )}

        <div>
          <label className="block text-[11px] font-semibold text-slate-400 uppercase tracking-wider mb-1">
            Quantity
          </label>
          <input
            type="number"
            min="1"
            max={action === 'SELL' ? ownedQty || 1 : 10000}
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            className="w-full bg-slate-900 border border-slate-750 rounded-xl px-3 py-2 text-white font-medium text-xs focus:outline-none focus:border-indigo-500"
          />
        </div>

        <div className="p-3 bg-slate-900/60 rounded-xl border border-slate-800 space-y-1 text-xs">
          <div className="flex justify-between text-slate-400">
            <span>Estimated Total:</span>
            <span className="font-bold text-white">
              ₹{estimatedTotal.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </span>
          </div>
          <div className="text-[10px] text-slate-500 text-right">
            Price based on simulated Phase 6 feed
          </div>
        </div>

        <button
          type="submit"
          disabled={executing || loadingQuote || currentPrice <= 0}
          className={`w-full py-2.5 rounded-xl font-bold text-xs uppercase tracking-wider transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-md ${
            action === 'BUY'
              ? 'bg-emerald-600 hover:bg-emerald-500 text-white'
              : 'bg-rose-600 hover:bg-rose-500 text-white'
          }`}
        >
          {executing ? 'Executing Order...' : `Execute ${action} Order`}
        </button>
      </form>
    </div>
  );
}
