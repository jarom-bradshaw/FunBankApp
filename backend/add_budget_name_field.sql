-- Add name field to budgets table to match our model
ALTER TABLE budgets ADD COLUMN name VARCHAR(100) AFTER user_id;
ALTER TABLE budgets ADD COLUMN category VARCHAR(100) AFTER name; 