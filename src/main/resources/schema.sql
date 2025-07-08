-- Main Schema for FunBankApp
-- This file is automatically loaded by Spring Boot

-- Investments Table
CREATE TABLE IF NOT EXISTS investments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    investment_type VARCHAR(50) NOT NULL,
    symbol VARCHAR(20),
    shares DECIMAL(15,6) NOT NULL,
    purchase_price DECIMAL(15,2) NOT NULL,
    current_price DECIMAL(15,2),
    purchase_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for investments table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_investments_user_id ON investments (user_id);
-- CREATE INDEX idx_investments_investment_type ON investments (investment_type);
-- CREATE INDEX idx_investments_symbol ON investments (symbol);
-- CREATE INDEX idx_investments_purchase_date ON investments (purchase_date);

-- Portfolios Table
CREATE TABLE IF NOT EXISTS portfolios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    target_allocation JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for portfolios table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_portfolios_user_id ON portfolios (user_id);
-- CREATE INDEX idx_portfolios_name ON portfolios (name);

-- Portfolio Holdings Table
CREATE TABLE IF NOT EXISTS portfolio_holdings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    investment_id BIGINT NOT NULL,
    target_percentage DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for portfolio_holdings table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_portfolio_holdings_portfolio_id ON portfolio_holdings (portfolio_id);
-- CREATE INDEX idx_portfolio_holdings_investment_id ON portfolio_holdings (investment_id);
-- CREATE UNIQUE INDEX unique_portfolio_investment ON portfolio_holdings (portfolio_id, investment_id);

-- Monte Carlo Simulations Table
CREATE TABLE IF NOT EXISTS monte_carlo_simulations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    simulation_type VARCHAR(50) NOT NULL,
    parameters JSON NOT NULL,
    results JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for monte_carlo_simulations table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_monte_carlo_simulations_user_id ON monte_carlo_simulations (user_id);
-- CREATE INDEX idx_monte_carlo_simulations_simulation_type ON monte_carlo_simulations (simulation_type);
-- CREATE INDEX idx_monte_carlo_simulations_created_at ON monte_carlo_simulations (created_at);

-- Investment Performance Table
CREATE TABLE IF NOT EXISTS investment_performance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    investment_id BIGINT NOT NULL,
    date DATE NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    volume BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for investment_performance table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_investment_performance_investment_id ON investment_performance (investment_id);
-- CREATE INDEX idx_investment_performance_date ON investment_performance (date);
-- CREATE UNIQUE INDEX unique_investment_date ON investment_performance (investment_id, date);

-- Asset Allocation Table
CREATE TABLE IF NOT EXISTS asset_allocations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    asset_type VARCHAR(50) NOT NULL,
    target_percentage DECIMAL(5,2) NOT NULL,
    actual_percentage DECIMAL(5,2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for asset_allocations table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_asset_allocations_user_id ON asset_allocations (user_id);
-- CREATE INDEX idx_asset_allocations_portfolio_id ON asset_allocations (portfolio_id);
-- CREATE INDEX idx_asset_allocations_asset_type ON asset_allocations (asset_type);
-- CREATE UNIQUE INDEX unique_user_asset_type ON asset_allocations (user_id, asset_type);

-- Investment Transactions Table
CREATE TABLE IF NOT EXISTS investment_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    investment_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    shares DECIMAL(15,6) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    transaction_date DATE NOT NULL,
    fees DECIMAL(15,2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for investment_transactions table (commented out to avoid duplicate key errors in MySQL)
-- CREATE INDEX idx_investment_transactions_investment_id ON investment_transactions (investment_id);
-- CREATE INDEX idx_investment_transactions_transaction_type ON investment_transactions (transaction_type);
-- CREATE INDEX idx_investment_transactions_date ON investment_transactions (date); 