-- V20250402__replace_loan_request_entity.sql

-- Step 1: Drop the foreign key constraint from loan_offer_entity that references loan_request_entity
ALTER TABLE loan_offer_entity
    DROP CONSTRAINT IF EXISTS fkiguqxekno4kels0kesuwo2bh7;

-- Step 2: Drop the existing loan_request_entity_new if it exists
DROP TABLE IF EXISTS loan_request_entity_new;

-- Step 3: Create the new loan_request_entity_new table
CREATE TABLE loan_request_entity_new (
                                         id VARCHAR(255) NOT NULL PRIMARY KEY,
                                         loanee_entity_id VARCHAR(255),
                                         loan_request_decision VARCHAR(50),
                                         created_date TIMESTAMP,
                                         date_time_approved TIMESTAMP,
                                         loan_amount_approved NUMERIC(38, 2),
                                         loan_amount_requested NUMERIC(38, 2),
                                         decline_reason VARCHAR(255),
                                         referred_by VARCHAR(255),
                                         status VARCHAR(255)
);

-- Step 4: Migrate data from the old table to the new one
INSERT INTO loan_request_entity_new (
    loan_request_decision,
    created_date,
    date_time_approved,
    loan_amount_approved,
    loan_amount_requested,
    decline_reason,
    id,
    loanee_entity_id,
    referred_by,
    status
)
SELECT
    loan_request_decision,
    created_date,
    date_time_approved,
    loan_amount_approved,
    loan_amount_requested,
    decline_reason,
    id,
    loanee_entity_id,
    referred_by,
    status
FROM loan_request_entity;

-- Step 5: Drop the old loan_request_entity table
DROP TABLE loan_request_entity;

-- Step 6: Rename the new table to the original name
ALTER TABLE loan_request_entity_new RENAME TO loan_request_entity;