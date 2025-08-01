-- Step 1: Drop the foreign key constraint dynamically
DO $$
DECLARE
constraint_name text;
BEGIN
SELECT tc.constraint_name INTO constraint_name
FROM information_schema.table_constraints tc
         JOIN information_schema.constraint_column_usage ccu
              ON tc.constraint_name = ccu.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_name = 'loan_offer_entity'
  AND ccu.table_name = 'loan_request_entity'
  AND ccu.column_name = 'id';

IF constraint_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE loan_offer_entity DROP CONSTRAINT ' || quote_ident(constraint_name);
END IF;
END $$;

-- Step 2: Recreate the foreign key with ON UPDATE CASCADE
ALTER TABLE loan_offer_entity
    ADD CONSTRAINT fk_loan_offer_loan_request
        FOREIGN KEY (loan_request_id) REFERENCES loan_request_entity (id)
            ON UPDATE CASCADE;

-- Step 3: Delete duplicate loan_request_entity rows, keeping the most recent
WITH ranked_requests AS (
    SELECT
        lr.id,
        ROW_NUMBER() OVER (PARTITION BY lre.id ORDER BY lr.created_date DESC) AS rn
    FROM loan_request_entity lr
             JOIN cohort_loanee_entity cle ON cle.loanee_id = lr.loanee_entity_id
             JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
    WHERE lr.loanee_entity_id IS NOT NULL
)
DELETE FROM loan_request_entity
WHERE id IN (
    SELECT id
    FROM ranked_requests
    WHERE rn > 1
);

-- Step 4: Update loan_request_entity.id to match loan_referral_entity.id
UPDATE loan_request_entity lr
SET id = (
    SELECT lre.id
    FROM loan_referral_entity lre
             JOIN cohort_loanee_entity cle ON lre.cohort_loanee_id = cle.id
    WHERE cle.loanee_id = lr.loanee_entity_id
)
WHERE lr.loanee_entity_id IS NOT NULL
  AND EXISTS (
    SELECT 1
    FROM loan_referral_entity lre
             JOIN cohort_loanee_entity cle ON lre.cohort_loanee_id = cle.id
    WHERE cle.loanee_id = lr.loanee_entity_id
);

-- Step 5: Drop the foreign key constraint on loanee_entity_id
ALTER TABLE loan_request_entity
DROP CONSTRAINT IF EXISTS fk_loan_request_loanee;

-- Step 6: Drop the loanee_entity_id column
ALTER TABLE loan_request_entity
DROP COLUMN loanee_entity_id;