// src/components/market/MarketChart.jsx
import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { getMarketHistory } from '../../api/endpoints';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const TIMEFRAMES = ['1D', '5D', '1M', '6M', '1Y'];

export default function MarketChart({ symbol = 'NIFTY50', displayName = 'NIFTY 50' }) {
  const [timeframe, setTimeframe] = useState('1D');
  const [candles, setCandles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let isMounted = true;

    getMarketHistory(symbol, timeframe)
      .then(res => {
        if (isMounted) {
          setCandles(res.data || []);
          setError('');
        }
      })
      .catch(() => {
        if (isMounted) {
          setError('Failed to load chart data.');
        }
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, [symbol, timeframe]);


  const prices = candles.map(c => c.close);
  const isPositive = prices.length > 1 ? prices[prices.length - 1] >= prices[0] : true;
  const strokeColor = isPositive ? 'rgba(16, 185, 129, 1)' : 'rgba(244, 63, 94, 1)';
  const fillColor = isPositive ? 'rgba(16, 185, 129, 0.12)' : 'rgba(244, 63, 94, 0.12)';

  const labels = candles.map(c => {
    const d = new Date(c.timestamp);
    if (timeframe === '1D') {
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return d.toLocaleDateString([], { month: 'short', day: 'numeric' });
  });

  const chartData = {
    labels,
    datasets: [
      {
        label: `${displayName} Price`,
        data: prices,
        borderColor: strokeColor,
        backgroundColor: fillColor,
        fill: true,
        tension: 0.25,
        borderWidth: 2,
        pointRadius: 0,
        pointHoverRadius: 4,
        pointHoverBackgroundColor: strokeColor,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        mode: 'index',
        intersect: false,
        backgroundColor: 'rgba(15, 23, 42, 0.95)',
        titleColor: '#94a3b8',
        bodyColor: '#f8fafc',
        borderColor: 'rgba(51, 65, 85, 0.5)',
        borderWidth: 1,
        callbacks: {
          label: (ctx) => ` ₹${ctx.parsed.y.toLocaleString('en-IN', { minimumFractionDigits: 2 })}`,
        },
      },
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { color: '#64748b', maxTicksLimit: 6, font: { size: 10 } },
      },
      y: {
        grid: { color: 'rgba(51, 65, 85, 0.25)' },
        ticks: {
          color: '#64748b',
          font: { size: 10 },
          callback: (val) => `₹${val.toLocaleString('en-IN')}`,
        },
      },
    },
  };

  return (
    <div className="bg-slate-900/60 rounded-xl p-4 border border-slate-800/80">
      <div className="flex flex-wrap items-center justify-between gap-2 mb-4">
        <div>
          <h4 className="text-white font-bold text-sm tracking-wide">{displayName} Performance</h4>
          <span className="text-[11px] text-slate-400">Historical trend (Simulated)</span>
        </div>
        <div className="flex items-center gap-1 bg-slate-800/80 p-1 rounded-lg border border-slate-700/60">
          {TIMEFRAMES.map(tf => (
            <button
              key={tf}
              onClick={() => setTimeframe(tf)}
              className={`px-2.5 py-1 text-xs font-semibold rounded transition-colors ${
                timeframe === tf
                  ? 'bg-indigo-600 text-white shadow'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              {tf}
            </button>
          ))}
        </div>
      </div>

      <div className="h-56 relative w-full">
        {loading && (
          <div className="absolute inset-0 bg-slate-900/70 backdrop-blur-sm flex items-center justify-center z-10 rounded-lg">
            <span className="w-5 h-5 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin" />
          </div>
        )}
        {error ? (
          <div className="h-full flex items-center justify-center text-xs text-rose-400">
            {error}
          </div>
        ) : candles.length === 0 && !loading ? (
          <div className="h-full flex items-center justify-center text-xs text-slate-500">
            No historical data available.
          </div>
        ) : (
          <Line data={chartData} options={chartOptions} />
        )}
      </div>
    </div>
  );
}
