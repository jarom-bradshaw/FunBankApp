-- Debt Management System Database Schema
-- This schema adds debt management capabilities to the existing FunBankApp database

-- =====================================================
-- DEBT TYPES ENUM (will be handled in application code)
-- =====================================================
-- CREDIT_CARD, MORTGAGE, STUDENT_LOAN, PERSONAL_LOAN, 
-- AUTO_LOAN, BUSINESS_LOAN, MEDICAL_DEBT, OTHER

-- =====================================================
-- DEBTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS debts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    debt_type VARCHAR(50) NOT NULL,
    original_balance DECIMAL(15,2) NOT NULL,
    current_balance DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,4) NOT NULL, -- Annual percentage rate (e.g., 0.1899 for 18.99%)
    minimum_payment DECIMAL(10,2),
    payment_due_day INT, -- Day of month payment is due (1-31)
    payment_frequency VARCHAR(20) DEFAULT 'MONTHLY', -- MONTHLY, BIWEEKLY, WEEKLY
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, PAID_OFF, DEFAULTED, SETTLED
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- HIGH, MEDIUM, LOW
    is_secured BOOLEAN DEFAULT FALSE,
    collateral_description TEXT,
    creditor_name VARCHAR(100),
    creditor_phone VARCHAR(20),
    creditor_website VARCHAR(255),
    account_number VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_debts_user_id (user_id),
    INDEX idx_debts_account_id (account_id),
    INDEX idx_debts_status (status),
    INDEX idx_debts_type (debt_type),
    INDEX idx_debts_due_day (payment_due_day)
);

-- =====================================================
-- DEBT PAYMENTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS debt_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debt_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    payment_amount DECIMAL(10,2) NOT NULL,
    principal_amount DECIMAL(10,2) NOT NULL,
    interest_amount DECIMAL(10,2) NOT NULL,
    fees_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_method VARCHAR(50),
    transaction_id BIGINT, -- Link to transactions table if applicable
    payment_status VARCHAR(20) DEFAULT 'COMPLETED', -- COMPLETED, PENDING, FAILED, CANCELLED
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    FOREIGN KEY (debt_id) REFERENCES debts(id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_debt_payments_debt_id (debt_id),
    INDEX idx_debt_payments_date (payment_date),
    INDEX idx_debt_payments_status (payment_status),
    INDEX idx_debt_payments_transaction_id (transaction_id)
);

-- =====================================================
-- DEBT STRATEGIES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS debt_strategies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    strategy_type VARCHAR(50) NOT NULL, -- AVALANCHE, SNOWBALL, DEBT_CONSOLIDATION, CUSTOM
    description TEXT,
    target_payoff_date DATE,
    estimated_interest_savings DECIMAL(15,2),
    estimated_payoff_time_months INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_debt_strategies_user_id (user_id),
    INDEX idx_debt_strategies_type (strategy_type),
    INDEX idx_debt_strategies_active (is_active)
);

-- =====================================================
-- DEBT STRATEGY DEBTS TABLE (Many-to-Many relationship)
-- =====================================================
CREATE TABLE IF NOT EXISTS debt_strategy_debts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    strategy_id BIGINT NOT NULL,
    debt_id BIGINT NOT NULL,
    priority_order INT NOT NULL, -- Order in which debts should be paid off
    target_payment_amount DECIMAL(10,2), -- Target payment for this debt in strategy
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    FOREIGN KEY (strategy_id) REFERENCES debt_strategies(id) ON DELETE CASCADE,
    FOREIGN KEY (debt_id) REFERENCES debts(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate debt-strategy combinations
    UNIQUE KEY unique_strategy_debt (strategy_id, debt_id),
    
    -- Indexes for performance
    INDEX idx_debt_strategy_debts_strategy_id (strategy_id),
    INDEX idx_debt_strategy_debts_debt_id (debt_id),
    INDEX idx_debt_strategy_debts_priority (priority_order)
);

-- =====================================================
-- DEBT REMINDERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS debt_reminders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    debt_id BIGINT NOT NULL,
    reminder_date DATE NOT NULL,
    reminder_type VARCHAR(50) NOT NULL, -- PAYMENT_DUE, PAYMENT_OVERDUE, RATE_CHANGE, BALANCE_UPDATE
    message TEXT NOT NULL,
    is_sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraints
    FOREIGN KEY (debt_id) REFERENCES debts(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_debt_reminders_debt_id (debt_id),
    INDEX idx_debt_reminders_date (reminder_date),
    INDEX idx_debt_reminders_sent (is_sent)
);

-- =====================================================
-- SAMPLE DATA FOR TESTING (Optional)
-- =====================================================
-- Uncomment the following lines to add sample data for testing

/*
-- Sample debt types for reference
INSERT INTO debts (user_id, name, description, debt_type, original_balance, current_balance, 
                   interest_rate, minimum_payment, payment_due_day, start_date, creditor_name)
VALUES 
(1, 'Chase Credit Card', 'Primary credit card', 'CREDIT_CARD', 5000.00, 4500.00, 0.1899, 150.00, 15, '2024-01-01', 'Chase Bank'),
(1, 'Student Loan', 'Federal student loan', 'STUDENT_LOAN', 25000.00, 24000.00, 0.0450, 250.00, 1, '2023-06-01', 'Federal Student Aid'),
(1, 'Car Loan', 'Auto loan for 2020 Honda', 'AUTO_LOAN', 15000.00, 12000.00, 0.0599, 300.00, 20, '2023-03-01', 'Honda Financial Services');
*/ 