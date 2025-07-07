-- Alter accounts table to add color column and rename type to account_type
-- This script should be run on the existing database

-- First, rename the 'type' column to 'account_type'
ALTER TABLE accounts CHANGE COLUMN type account_type VARCHAR(50) NOT NULL;

-- Then, add the 'color' column
ALTER TABLE accounts ADD COLUMN color VARCHAR(7);

-- Verify the changes
DESCRIBE accounts; 