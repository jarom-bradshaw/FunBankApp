-- Create missing tables
CREATE TABLE account_balance_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    change_amount DECIMAL(15,2) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE goal_milestones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goal_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE CASCADE
);

CREATE TABLE chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    response TEXT NOT NULL,
    context JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for the new tables
CREATE INDEX idx_account_balance_history_account_id ON account_balance_history(account_id);
CREATE INDEX idx_account_balance_history_date ON account_balance_history(date);
CREATE INDEX idx_chat_history_user_id ON chat_history(user_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id); 

-- Migration for accounts table to add color and rename type to account_type
ALTER TABLE accounts CHANGE COLUMN type account_type VARCHAR(50) NOT NULL;
ALTER TABLE accounts ADD COLUMN color VARCHAR(7); 