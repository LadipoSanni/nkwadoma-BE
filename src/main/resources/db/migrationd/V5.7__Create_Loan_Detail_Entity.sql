ALTER TABLE cohort_entity
    ADD COLUMN IF NOT EXISTS  still_In_Training INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS number_of_dropout INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS  number_employed INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS  number_of_loan_request INTEGER DEFAULT 0;


-- Enable UUID extension (unchanged)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the cohort_loan_detail_entity table (unchanged)
CREATE TABLE IF NOT EXISTS cohort_loan_detail_entity (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cohort_id VARCHAR(36) NOT NULL UNIQUE,
    total_amount_requested DECIMAL(19,2) DEFAULT 0.00,
    total_outstanding_amount DECIMAL(19,2) DEFAULT 0.00,
    total_amount_received DECIMAL(19,2) DEFAULT 0.00,
    total_amount_repaid DECIMAL(19,2) DEFAULT 0.00,
    FOREIGN KEY (cohort_id) REFERENCES cohort_entity(id)
    );

-- Insert data into cohort_loan_detail_entity for all existing cohorts
INSERT INTO cohort_loan_detail_entity (
    cohort_id,
    total_amount_requested,
    total_amount_received,
    total_amount_repaid,
    total_outstanding_amount
)
SELECT
    c.id,
    COALESCE((
                 SELECT SUM(lld.amount_requested)
                 FROM cohort_loanee_entity cl
                          JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
                 WHERE cl.cohort_id = c.id
             ), 0.0) AS total_amount_requested,
    COALESCE((
                 SELECT SUM(lo.amount_approved)
                 FROM cohort_loanee_entity cle
                          JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
                          JOIN loan_request_entity lr ON lr.id = lre.id
                          JOIN loan_offer_entity lo ON lo.id = lr.id
                          JOIN loan_entity loan ON loan.loan_offer_id = lo.id
                 WHERE cle.cohort_id = c.id
                   AND lo.loan_offer_status = 'OFFERED'
                   AND loan.loan_status = 'PERFORMING'
             ), 0.0) AS total_amount_received,
    COALESCE((
                 SELECT SUM(rh.amount_paid)
                 FROM repayment_history_entity rh
                          JOIN cohort_loanee_entity cle ON rh.cohort_id = cle.cohort_id
                          JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
                          JOIN loan_request_entity lr ON lr.id = lre.id
                          JOIN loan_offer_entity lo ON lo.id = lr.id
                          JOIN loan_entity loan ON loan.loan_offer_id = lo.id
                 WHERE rh.cohort_id = c.id
                   AND lo.loan_offer_status = 'OFFERED'
                   AND loan.loan_status = 'PERFORMING'
             ), 0.0) AS total_amount_repaid,
    COALESCE((
                 SELECT SUM(lo.amount_approved)
                 FROM cohort_loanee_entity cle
                          JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
                          JOIN loan_request_entity lr ON lr.id = lre.id
                          JOIN loan_offer_entity lo ON lo.id = lr.id
                          JOIN loan_entity loan ON loan.loan_offer_id = lo.id
                 WHERE cle.cohort_id = c.id
                   AND lo.loan_offer_status = 'OFFERED'
                   AND loan.loan_status = 'PERFORMING'
             ), 0.0) -
    COALESCE((
                 SELECT SUM(rh.amount_paid)
                 FROM repayment_history_entity rh
                          JOIN cohort_loanee_entity cle ON rh.cohort_id = cle.cohort_id
                          JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
                          JOIN loan_request_entity lr ON lr.id = lre.id
                          JOIN loan_offer_entity lo ON lo.id = lr.id
                          JOIN loan_entity loan ON loan.loan_offer_id = lo.id
                 WHERE rh.cohort_id = c.id
                   AND lo.loan_offer_status = 'OFFERED'
                   AND loan.loan_status = 'PERFORMING'
             ), 0.0) AS total_outstanding_amount
FROM cohort_entity c
WHERE NOT EXISTS (
    SELECT 1 FROM cohort_loan_detail_entity clde WHERE clde.cohort_id = c.id
);

UPDATE cohort_entity c
SET still_in_training = (
    SELECT COALESCE(COUNT(*), 0)
    FROM cohort_loanee_entity cl
    WHERE cl.cohort_id = c.id
)
WHERE EXISTS (
    SELECT 1
    FROM cohort_loanee_entity cl
    WHERE cl.cohort_id = c.id
    HAVING COUNT(*) != COALESCE(c.still_in_training, 0)
)
   OR (c.still_in_training IS NULL AND NOT EXISTS (
    SELECT 1 FROM cohort_loanee_entity cl WHERE cl.cohort_id = c.id
));



UPDATE cohort_entity c
SET number_of_loan_request = (
    SELECT COALESCE(COUNT(*), 0)
    FROM loan_request_entity lr
             JOIN loan_referral_entity lre ON lr.id = lre.id
             JOIN cohort_loanee_entity cle ON lre.cohort_loanee_id = cle.id
    WHERE lr.status = 'NEW' AND cle.cohort_id = c.id
)
WHERE EXISTS (
    SELECT 1
    FROM loan_request_entity lr
             JOIN loan_referral_entity lre ON lr.id = lre.id
             JOIN cohort_loanee_entity cle ON lre.cohort_loanee_id = cle.id
    WHERE cle.cohort_id = c.id AND lr.status = 'NEW'
    HAVING COUNT(*) != COALESCE(c.number_of_loan_request, 0)
)
   OR (
    c.number_of_loan_request IS NULL
        AND NOT EXISTS (
        SELECT 1
        FROM loan_request_entity lr
                 JOIN loan_referral_entity lre ON lr.id = lre.id
                 JOIN cohort_loanee_entity cle ON lre.cohort_loanee_id = cle.id
        WHERE cle.cohort_id = c.id AND lr.status = 'NEW'
    )
    );

