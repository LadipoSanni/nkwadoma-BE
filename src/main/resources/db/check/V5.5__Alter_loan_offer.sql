-- Drop foreign key constraints if they exist
ALTER TABLE loan_offer_entity
DROP CONSTRAINT IF EXISTS fk_loan_offer_loan_request;

ALTER TABLE loan_offer_entity
DROP CONSTRAINT IF EXISTS fk_loan_offer_loanee;

-- Update loan_offer_entity id to match loan_request_entity id
UPDATE loan_offer_entity loe
SET id = (
    SELECT lre.id
    FROM loan_request_entity lre
    WHERE lre.id = loe.loan_request_id
)
WHERE EXISTS (
    SELECT 1
    FROM loan_request_entity lre
    WHERE lre.id = loe.loan_request_id
);

-- Drop old columns
ALTER TABLE loan_offer_entity
DROP COLUMN loanee_id,
DROP COLUMN loan_request_id;