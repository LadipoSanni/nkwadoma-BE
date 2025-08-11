ALTER TABLE loanee_loan_aggregate_entity
    ADD COLUMN IF NOT EXISTS total_amount_repaid DECIMAL(19,2) DEFAULT 0.00;


INSERT INTO loanee_loan_aggregate_entity (total_amount_repaid)
SELECT
    COALESCE(SUM(lld.amount_repaid), 0.00) AS total_amount_repaid
FROM loanee_entity l
         JOIN cohort_loanee_entity cl ON l.id = cl.loanee_id
         JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
         JOIN loanee_loan_aggregate_entity llae ON llae.loanee_id = l.id
GROUP BY l.id;