import React, { useState, useEffect } from 'react';
import {
  getTradingWallet,
  getTradingHoldings,
  getTradingPortfolio,
  getTradingTransactions,
} from '../../api/endpoints';
import TradingDisclaimer from './TradingDisclaimer';
import WalletCard from './WalletCard';
import PortfolioSummary from './PortfolioSummary';
import HoldingsTable from './HoldingsTable';
import OrderPanel from './OrderPanel';
import TransactionHistory from './TransactionHistory';

export default function TradingDashboard() {
  const [wallet, setWallet] = useState(null);
  const [holdings, setHoldings] = useState([]);
  const [portfolio, setPortfolio] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [selectedStock, setSelectedStock] = useState('RELIANCE');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchTradingData = () => {
    Promise.all([
      getTradingWallet(),
      getTradingHoldings(),
      getTradingPortfolio(),
      getTradingTransactions(),
    ])
      .then(([walletRes, holdingsRes, portfolioRes, txRes]) => {
        setWallet(walletRes.data);
        setHoldings(holdingsRes.data || []);
        setPortfolio(portfolioRes.data);
        setTransactions(txRes.data || []);
        setError('');
      })
      .catch((err) => {
        setError(err.response?.data?.message || 'Failed to load trading simulator data.');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchTradingData();
  }, []);

  if (loading && !wallet) {
    return (
      <div className="bg-slate-800/80 border border-slate-700/60 rounded-2xl p-8 mb-8 text-center">
        <div className="flex items-center justify-center gap-2 text-slate-400 text-sm">
          <span className="w-4 h-4 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin" />
          Initializing Virtual Trading Desk...
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 mb-8">
      <TradingDisclaimer />

      {error && (
        <div className="bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs p-3 rounded-xl flex items-center justify-between">
          <span>{error}</span>
          <button
            onClick={fetchTradingData}
            className="text-xs font-bold underline hover:opacity-80 ml-2"
          >
            Retry
          </button>
        </div>
      )}

      {/* Top summary row: Wallet & Portfolio */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="md:col-span-1">
          <WalletCard wallet={wallet} />
        </div>
        <div className="md:col-span-2">
          <PortfolioSummary portfolio={portfolio} />
        </div>
      </div>

      {/* Middle section: Holdings and Order Panel */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <HoldingsTable
            holdings={holdings}
            onSelectStock={(sym) => setSelectedStock(sym)}
          />
        </div>
        <div className="lg:col-span-1">
          <OrderPanel
            selectedSymbol={selectedStock}
            onTradeSuccess={fetchTradingData}
            walletCash={wallet?.cashBalance}
            holdings={holdings}
          />
        </div>
      </div>

      {/* Bottom section: Transaction History */}
      <TransactionHistory transactions={transactions} />
    </div>
  );
}
