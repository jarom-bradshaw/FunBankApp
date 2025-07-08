-- Drop existing tables if they exist
DROP TABLE IF EXISTS debt_reminders;
DROP TABLE IF EXISTS debt_strategy_debts;
DROP TABLE IF EXISTS debt_strategies;
DROP TABLE IF EXISTS debt_payments;
DROP TABLE IF EXISTS debts;

-- Create debts table
CREATE TABLE debts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    debt_type VARCHAR(50) NOT NULL,
    original_balance DECIMAL(15,2) NOT NULL,
    current_balance DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,4) NOT NULL,
    minimum_payment DECIMAL(10,2),
    payment_due_day INT,
    payment_frequency VARCHAR(20) DEFAULT 'MONTHLY',
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    is_secured BOOLEAN DEFAULT FALSE,
    creditor_name VARCHAR(100),
    account_number VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    INDEX idx_debts_user_id (user_id),
    INDEX idx_debts_status (status)
);

-- Create debt_payments table
CREATE TABLE debt_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debt_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    payment_amount DECIMAL(10,2) NOT NULL,
    principal_amount DECIMAL(10,2) NOT NULL,
    interest_amount DECIMAL(10,2) NOT NULL,
    fees_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_method VARCHAR(50),
    transaction_id BIGINT,
    payment_status VARCHAR(20) DEFAULT 'COMPLETED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (debt_id) REFERENCES debts(id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,
    INDEX idx_debt_payments_debt_id (debt_id),
    INDEX idx_debt_payments_date (payment_date)
);

-- Create debt_strategies table
CREATE TABLE debt_strategies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    strategy_type VARCHAR(50) NOT NULL,
    description TEXT,
    target_payoff_date DATE,
    estimated_interest_savings DECIMAL(15,2),
    estimated_payoff_time_months INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_debt_strategies_user_id (user_id)
);

-- Create debt_strategy_debts table
CREATE TABLE debt_strategy_debts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    strategy_id BIGINT NOT NULL,
    debt_id BIGINT NOT NULL,
    priority_order INT NOT NULL,
    target_payment_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (strategy_id) REFERENCES debt_strategies(id) ON DELETE CASCADE,
    FOREIGN KEY (debt_id) REFERENCES debts(id) ON DELETE CASCADE,
    UNIQUE KEY unique_strategy_debt (strategy_id, debt_id)
); 