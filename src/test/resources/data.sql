-- Seed test data for export functionality tests

-- Insert test transactions
INSERT INTO transactions (user_id, amount, description, category, transaction_date, type, created_at, updated_at) VALUES
(1, 1500.00, 'Salary Deposit', 'income', '2024-01-15', 'income', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, -75.50, 'Grocery Shopping', 'food', '2024-01-16', 'expense', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, -45.00, 'Gas Station', 'transportation', '2024-01-17', 'expense', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, -120.00, 'Electric Bill', 'utilities', '2024-01-18', 'expense', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, -200.00, 'Restaurant Dinner', 'entertainment', '2024-01-19', 'expense', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test debts
INSERT INTO debts (user_id, name, amount, interest_rate, minimum_payment, due_date, debt_type, priority, status, created_at, updated_at) VALUES
(1, 'Credit Card 1', 2500.00, 18.99, 75.00, '2024-02-15', 'credit_card', 'high', 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Student Loan', 15000.00, 5.50, 200.00, '2024-02-01', 'student_loan', 'medium', 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Car Loan', 8000.00, 4.25, 300.00, '2024-02-10', 'car_loan', 'low', 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test budgets
INSERT INTO budgets (user_id, name, amount, period, category, start_date, end_date, created_at, updated_at) VALUES
(1, 'Monthly Groceries', 400.00, 'monthly', 'food', '2024-01-01', '2024-01-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Entertainment Budget', 200.00, 'monthly', 'entertainment', '2024-01-01', '2024-01-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Transportation Budget', 150.00, 'monthly', 'transportation', '2024-01-01', '2024-01-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test goals
INSERT INTO goals (user_id, name, target_amount, current_amount, target_date, category, priority, status, created_at, updated_at) VALUES
(1, 'Emergency Fund', 10000.00, 3500.00, '2024-12-31', 'savings', 'high', 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Vacation Fund', 5000.00, 1200.00, '2024-06-30', 'travel', 'medium', 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'New Car Down Payment', 8000.00, 0.00, '2025-03-31', 'vehicle', 'low', 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test accounts
INSERT INTO accounts (user_id, name, type, balance, currency, created_at, updated_at) VALUES
(1, 'Main Checking', 'checking', 2500.00, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Savings Account', 'savings', 3500.00, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Credit Card', 'credit', -2500.00, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 