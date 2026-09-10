-- =============================================================
-- FinQuest — V9: Virtual Trading
-- Three tables for the educational virtual trading simulator.
-- No real money. No broker APIs. Simulated market prices only.
-- =============================================================

-- ── virtual_wallets ──────────────────────────────────────────
-- 1:1 — one virtual cash wallet per user.
-- Created atomically on first wallet request (INSERT IGNORE strategy).
CREATE TABLE virtual_wallets (
    id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    cash_balance    DECIMAL(15,2) NOT NULL DEFAULT 100000.00,
    initial_balance DECIMAL(15,2) NOT NULL DEFAULT 100000.00,
    created_at      DATETIME(6)   NOT NULL,
    updated_at      DATETIME(6)   NOT NULL,
    CONSTRAINT fk_vwallet_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_vwallet_user UNIQUE (user_id)
);

-- ── virtual_holdings ─────────────────────────────────────────
-- 1:N — one row per (user, symbol) combination.
-- quantity >= 1 always; row deleted when quantity drops to zero.
-- average_buy_price: 4dp precision for accurate cost-basis tracking.
CREATE TABLE virtual_holdings (
    id                BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT        NOT NULL,
    symbol            VARCHAR(20)   NOT NULL,
    quantity          INT           NOT NULL,
    average_buy_price DECIMAL(15,4) NOT NULL,
    created_at        DATETIME(6)   NOT NULL,
    updated_at        DATETIME(6)   NOT NULL,
    CONSTRAINT fk_vholding_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_vholding_user_symbol UNIQUE (user_id, symbol)
);

-- ── virtual_transactions ─────────────────────────────────────
-- Immutable ledger — one row per executed trade.
-- realized_profit_loss: populated on SELL only (NULL for BUY).
CREATE TABLE virtual_transactions (
    id                   BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT        NOT NULL,
    symbol               VARCHAR(20)   NOT NULL,
    transaction_type     VARCHAR(4)    NOT NULL,    -- 'BUY' | 'SELL'
    quantity             INT           NOT NULL,
    execution_price      DECIMAL(15,4) NOT NULL,
    total_value          DECIMAL(15,2) NOT NULL,
    realized_profit_loss DECIMAL(15,2) NULL,        -- SELL only
    timestamp            DATETIME(6)   NOT NULL,
    CONSTRAINT fk_vtx_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── Indexes ───────────────────────────────────────────────────
CREATE INDEX idx_vholding_user        ON virtual_holdings(user_id);
CREATE INDEX idx_vtx_user             ON virtual_transactions(user_id);
CREATE INDEX idx_vtx_user_symbol      ON virtual_transactions(user_id, symbol);
CREATE INDEX idx_vtx_timestamp        ON virtual_transactions(timestamp);
