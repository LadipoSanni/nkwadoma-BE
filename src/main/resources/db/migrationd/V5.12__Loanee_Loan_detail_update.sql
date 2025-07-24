-- Updating LoaneeLoanDetailEntity for each CohortLoaneeEntity that has a related LoanEntity
UPDATE loanee_loan_detail_entity lld
SET
    amount_received = (
        SELECT lo.amount_approved
        FROM loan_offer_entity lo
                 JOIN loan_entity l ON l.loan_offer_id = lo.id
                 JOIN cohort_loanee_entity cl ON cl.loanee_loan_detail_id = lld.id
        WHERE lld.id = cl.loanee_loan_detail_id
    LIMIT 1
    ),
    amount_repaid = (
SELECT COALESCE(SUM(rh.amount_paid), 0)
FROM repayment_history_entity rh
    JOIN cohort_loanee_entity cl ON cl.loanee_loan_detail_id = lld.id
WHERE rh.loanee_id = cl.loanee_id
  AND rh.cohort_id = cl.cohort_id
    ),
    amount_outstanding = (
SELECT
    COALESCE((
    SELECT lo.amount_approved
    FROM loan_offer_entity lo
    JOIN loan_entity l ON l.loan_offer_id = lo.id
    JOIN cohort_loanee_entity cl ON cl.loanee_loan_detail_id = lld.id
    WHERE lld.id = cl.loanee_loan_detail_id
    LIMIT 1
    ), 0) -
    COALESCE((
    SELECT SUM(rh.amount_paid)
    FROM repayment_history_entity rh
    JOIN cohort_loanee_entity cl ON cl.loanee_loan_detail_id = lld.id
    WHERE rh.loanee_id = cl.loanee_id
    AND rh.cohort_id = cl.cohort_id
    ), 0)
    ),
    tuition_amount = (
SELECT c.tuition_amount
FROM cohort_entity c
    JOIN cohort_loanee_entity cl ON cl.cohort_id = c.id
WHERE cl.loanee_loan_detail_id = lld.id
    LIMIT 1
    )
WHERE EXISTS (
    SELECT 1
    FROM cohort_loanee_entity cl
    JOIN loan_entity l ON l.loan_offer_id = (
    SELECT lo.id
    FROM loan_offer_entity lo
    WHERE lo.id = l.loan_offer_id
    )
    WHERE cl.loanee_loan_detail_id = lld.id
    );