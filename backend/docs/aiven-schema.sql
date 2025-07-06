-- Financial Accounts
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    account_type VARCHAR(50) NOT NULL, -- renamed from 'type' to 'account_type'
    color VARCHAR(7), -- added for frontend color support
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
    INDEX idx_account_type (account_type),
    INDEX idx_account_status (account_status)
); 