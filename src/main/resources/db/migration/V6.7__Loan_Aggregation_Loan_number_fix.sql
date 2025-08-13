UPDATE loanee_loan_aggregate_entity lla
SET
    historical_debt = agg.historical_debt,
    total_amount_outstanding = agg.total_amount_outstanding,
    number_of_loans = agg.number_of_loans,
    total_amount_repaid = agg.total_amount_repaid
    FROM (
    SELECT
        l.id AS loanee_id,
        COALESCE(SUM(CASE WHEN lld.amount_received IS NOT NULL AND lld.amount_received > 0
                         THEN lld.amount_received ELSE 0 END), 0) AS historical_debt,
        COALESCE(SUM(CASE WHEN lld.amount_received IS NOT NULL AND lld.amount_received > 0
                         THEN lld.amount_outstanding ELSE 0 END), 0) AS total_amount_outstanding,
        COUNT(CASE WHEN lld.amount_received IS NOT NULL AND lld.amount_received > 0
                   THEN lld.id END) AS number_of_loans,
        COALESCE(SUM(CASE WHEN lld.amount_received IS NOT NULL AND lld.amount_received > 0
                   THEN lld.amount_repaid ELSE 0 END), 0) AS total_amount_repaid
    FROM loanee_entity l
    JOIN cohort_loanee_entity cl ON l.id = cl.loanee_id
    JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
    GROUP BY l.id
) agg
WHERE lla.loanee_id = agg.loanee_id;
