WITH loan_summary AS (
    SELECT
        l.id AS loanee_id,
        SUM(lld.amount_received) AS historical_debt,
        SUM(lld.amount_outstanding) AS total_amount_outstanding,
        SUM(lld.amount_repaid) AS total_amount_repaid,
        COUNT(lld.id) AS number_of_loans
    FROM loanee_entity l
             JOIN cohort_loanee_entity cl ON cl.loanee_id = l.id
             JOIN loanee_loan_detail_entity lld ON lld.id = cl.loanee_loan_detail_id
    WHERE lld.amount_received IS NOT NULL
      AND lld.amount_received > 0
    GROUP BY l.id
)
UPDATE loanee_loan_aggregate_entity lla
SET
    historical_debt = COALESCE(ls.historical_debt, 0),
    total_amount_outstanding = COALESCE(ls.total_amount_outstanding, 0),
    number_of_loans = COALESCE(ls.number_of_loans, 0),
    total_amount_repaid = COALESCE(ls.total_amount_repaid, 0)
    FROM loan_summary ls
WHERE lla.loanee_id = ls.loanee_id;
