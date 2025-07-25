-- Add new columns to loanee_loan_detail_entity
ALTER TABLE loanee_loan_detail_entity
ADD COLUMN IF NOT EXISTS loan_start_date TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

-- Update loanee_loan_detail_entity created_at from related tables
UPDATE loanee_loan_detail_entity llde
SET created_at = cle.created_at

    FROM cohort_loanee_entity cle
    JOIN loanee_loan_detail_entity llde2 ON llde2.id = cle.loanee_loan_detail_id
WHERE llde.id = llde2.id;

-- Update loanee_loan_detail_entity start_date from related tables
UPDATE loanee_loan_detail_entity llde
SET loan_start_date = l.start_date

    FROM cohort_loanee_entity cle
    JOIN loanee_loan_detail_entity llde2 ON llde2.id = cle.loanee_loan_detail_id
    JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
    JOIN loan_request_entity lr ON lr.id = lre.id
    JOIN loan_offer_entity lo ON lo.id = lr.id
    JOIN loan_product lp ON lp.id = lo.loan_product_id
    JOIN loan_entity l ON l.loan_offer_id = lo.id
WHERE llde.id = llde2.id;


-- Update loanee_loan_detail_entity interest_rate from related tables
UPDATE loanee_loan_detail_entity llde
SET interest_rate = lp.interest_rate

    FROM cohort_loanee_entity cle
    JOIN loanee_loan_detail_entity llde2 ON llde2.id = cle.loanee_loan_detail_id
    JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
    JOIN loan_request_entity lr ON lr.id = lre.id
    JOIN loan_offer_entity lo ON lo.id = lr.id
    JOIN loan_product lp ON lp.id = lo.loan_product_id

WHERE llde.id = llde2.id;