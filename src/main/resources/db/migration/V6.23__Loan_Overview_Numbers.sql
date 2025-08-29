ALTER TABLE loan_metrics_entity
    ADD COLUMN IF NOT EXISTS uploaded_loan_count INTEGER NOT NULL DEFAULT 0;

UPDATE loan_metrics_entity lme
SET uploaded_loan_count = (
    SELECT COUNT(cle.id)
    FROM loan_metrics_entity lme_inner
             JOIN organization org ON org.id = lme_inner.organization_id
             JOIN program_entity pro ON pro.organization_identity_id = org.id
             JOIN cohort_entity coh ON coh.program_id = pro.id
             JOIN cohort_loanee_entity cle ON cle.cohort_id = coh.id
    WHERE coh.cohort_type = 'LOAN_BOOK'
    AND lme_inner.id = lme.id
);


-- Alter table to add columns
ALTER TABLE portfolio_entity
    ADD COLUMN IF NOT EXISTS historical_debt DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS disbursed_loan_amount DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS net_loan_portfolio DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_amount_earned DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS number_of_loanees INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS number_of_organizations INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS number_of_loan_products INTEGER NOT NULL DEFAULT 0;

-- Update portfolio_entity with global aggregates
UPDATE portfolio_entity pe
SET historical_debt = (
    SELECT COALESCE(SUM(llae.amount_received), 0)
    FROM loanee_loan_detail_entity llae
),
    disbursed_loan_amount = (
        SELECT COALESCE(SUM(lo.amount_approved), 0)
        FROM loan_offer_entity lo
                 JOIN loan_entity loe ON loe.loan_offer_id = lo.id
    ),
    number_of_loanees = (
        SELECT COALESCE(COUNT(l.id), 0)
        FROM loanee_entity l
    ),
    number_of_organizations = (
        SELECT COALESCE(COUNT(o.id), 0)
        FROM organization o
    ),
    number_of_loan_products = (
        SELECT COALESCE(COUNT(lp.id), 0)
        FROM loan_product lp
    );



ALTER TABLE cohort_loanee_entity
    ADD COLUMN IF NOT EXISTS onboarding_mode VARCHAR(50) NOT NULL DEFAULT 'EMAIL_REFERRED';

UPDATE cohort_loanee_entity cle
SET onboarding_mode = 'FILE_UPLOADED_FOR_DISBURSED_LOANS'
WHERE EXISTS (
    SELECT ce
    FROM cohort_entity ce
    WHERE ce.id = cle.cohort_id AND ce.cohort_type = 'LOAN_BOOK'
);