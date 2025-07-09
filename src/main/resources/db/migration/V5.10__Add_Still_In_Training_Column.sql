-- Add the stillInTraining column with a default value of 0
ALTER TABLE organization
    ADD COLUMN still_in_training INTEGER NOT NULL DEFAULT 0;

-- Update stillInTraining with the values from numberOfLoanees
UPDATE organization
SET still_in_training = number_of_loanees;