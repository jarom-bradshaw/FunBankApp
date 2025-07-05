-- Aiven MySQL Schema for FunBankApp
-- This schema uses the existing 'defaultdb' database provided by Aiven

-- Use the existing database (don't create a new one)
USE defaultdb;

-- Core User Management
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    date_of_birth DATE,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    employment_status VARCHAR(50),
    annual_income DECIMAL(15,2),
    risk_tolerance VARCHAR(20),
    investment_experience VARCHAR(20),
    notification_preferences JSON,
    subscription_status VARCHAR(20),
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    last_password_change TIMESTAMP,
    account_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    preferred_language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    profile_picture_url VARCHAR(255),
    last_activity TIMESTAMP,
    account_verification_status VARCHAR(20) DEFAULT 'PENDING',
    verification_token VARCHAR(100),
    verification_token_expiry TIMESTAMP,
    password_reset_token VARCHAR(100),
    password_reset_token_expiry TIMESTAMP,
    login_attempts INT DEFAULT 0,
    account_locked_until TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_type VARCHAR(20),
    password_expiry_date TIMESTAMP,
    terms_accepted BOOLEAN DEFAULT FALSE,
    terms_accepted_date TIMESTAMP,
    privacy_policy_accepted BOOLEAN DEFAULT FALSE,
    privacy_policy_accepted_date TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username),
    INDEX idx_account_status (account_status)
);

-- Financial Accounts
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    institution_name VARCHAR(100),
    account_number VARCHAR(50),
    routing_number VARCHAR(50),
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    interest_rate DECIMAL(5,2),
    interest_compounding_frequency VARCHAR(20),
    minimum_balance DECIMAL(15,2),
    overdraft_protection BOOLEAN DEFAULT FALSE,
    account_status VARCHAR(20) DEFAULT 'ACTIVE',
    account_holder_type VARCHAR(20),
    tax_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_updated TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_account_type (type),
    INDEX idx_account_status (account_status)
);

-- Transactions
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category_id BIGINT,
    merchant_name VARCHAR(100),
    merchant_category_code VARCHAR(10),
    description TEXT,
    transaction_date TIMESTAMP NOT NULL,
    payment_method VARCHAR(50),
    transaction_status VARCHAR(20) DEFAULT 'COMPLETED',
    tax_category VARCHAR(50),
    tax_deductible BOOLEAN DEFAULT FALSE,
    receipt_url VARCHAR(255),
    recurring_id BIGINT,
    recurring_frequency VARCHAR(20),
    recurring_end_date DATE,
    tax_year INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    INDEX idx_account_id (account_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_category_id (category_id),
    INDEX idx_recurring_id (recurring_id)
);

-- Categories
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    parent_id BIGINT,
    color VARCHAR(7),
    icon VARCHAR(50),
    is_system BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type)
);

-- Budgets
CREATE TABLE budgets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    period VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    rollover BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_period (period)
);

-- Financial Goals
CREATE TABLE goals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    target_amount DECIMAL(15,2) NOT NULL,
    current_amount DECIMAL(15,2) DEFAULT 0.00,
    deadline DATE,
    type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    priority VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

-- Add some sample data for testing
INSERT INTO users (username, email, password_hash, first_name, last_name, account_status) VALUES
('testuser', 'test@example.com', '$2a$10$dummy.hash.for.testing', 'Test', 'User', 'ACTIVE');

INSERT INTO categories (user_id, name, type, is_system) VALUES
(1, 'Food & Dining', 'EXPENSE', TRUE),
(1, 'Transportation', 'EXPENSE', TRUE),
(1, 'Entertainment', 'EXPENSE', TRUE),
(1, 'Salary', 'INCOME', TRUE);

-- Show tables to verify creation
SHOW TABLES; 