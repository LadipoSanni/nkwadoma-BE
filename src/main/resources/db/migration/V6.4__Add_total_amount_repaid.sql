ALTER TABLE loanee_loan_aggregate_entity
    ADD COLUMN IF NOT EXISTS total_amount_repaid DECIMAL(19,2) DEFAULT 0.00;

UPDATE loanee_loan_aggregate_entity llae
SET total_amount_repaid = sub_query.total_amount_repaid
    FROM (
    SELECT
        l.id AS loanee_id,
        COALESCE(SUM(lld.amount_repaid), 0.00) AS total_amount_repaid
    FROM loanee_entity l
    JOIN cohort_loanee_entity cl ON l.id = cl.loanee_id
    JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
    GROUP BY l.id
) sub_query
WHERE llae.loanee_id = sub_query.loanee_id;

