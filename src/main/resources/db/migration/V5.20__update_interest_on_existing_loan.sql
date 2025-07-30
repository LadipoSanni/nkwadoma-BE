UPDATE loanee_loan_detail_entity llde
SET
--     interest_incurred = (
--         CASE
--             WHEN loan_start_date IS NOT NULL and updated_at IS NULL
--                 AND interest_incurred = 0 AND amount_outstanding > 0
--                 THEN COALESCE(interest_incurred, 0) + (
--                 amount_outstanding * (interest_rate / 100.0 / 365.0) *
--                 (DATE_PART('day', AGE(CURRENT_DATE, loan_start_date)) - 1)
--                 )
--
--             WHEN loan_start_date IS NOT NULL and  updated_at IS NOT NULL
--                 AND amount_outstanding > 0 AND interest_incurred = 0 AND amount_repaid = 0
--                 THEN COALESCE(interest_incurred, 0) + (
--                 amount_outstanding * (interest_rate / 100.0 / 365.0) *
--                 (DATE_PART('day', AGE(CURRENT_DATE, loan_start_date)) - 1)
--                 )
--
--             WHEN updated_at IS NULL AND loan_start_date IS NOT NULL
--                 AND interest_incurred > 0 AND amount_outstanding > 0 AND amount_repaid > 0
--                 THEN (
--                 COALESCE(amount_received, 0) * (interest_rate / 100.0 / 365.0) *
--                 (DATE_PART('day', AGE(CURRENT_DATE, loan_start_date)) - 1)
--                 )
--
--             WHEN loan_start_date IS NOT NULL and  updated_at IS NOT NULL
--                  AND amount_outstanding > 0 AND interest_incurred > 0
--                  THEN COALESCE(interest_incurred, 0) + (
--                  amount_outstanding * (interest_rate / 100.0 / 365.0) *
--                  (DATE_PART('day', AGE(CURRENT_DATE, updated_at::date)) - 1)
--                  )
--
--             ELSE COALESCE(interest_incurred, 0)
--             END)
--         ),
    amount_outstanding =
        CASE
             WHEN loan_start_date IS NOT NULL and updated_at IS NULL
             AND interest_incurred = 0 AND amount_outstanding > 0
             THEN COALESCE(amount_outstanding, 0) + COALESCE(interest_incurred, 0)

             WHEN loan_start_date IS NOT NULL and  updated_at IS NOT NULL
             AND amount_outstanding > 0 AND interest_incurred = 0 AND amount_repaid = 0
             THEN COALESCE(amount_outstanding, 0) + COALESCE(interest_incurred, 0)

             WHEN updated_at IS NULL AND loan_start_date IS NOT NULL
             AND interest_incurred > 0 AND amount_outstanding > 0 AND amount_repaid > 0
             THEN COALESCE(amount_received, 0) - COALESCE(amount_repaid, 0) + COALESCE(interest_incurred, 0)

             WHEN loan_start_date IS NOT NULL and  updated_at IS NOT NULL
             AND amount_outstanding > 0 AND interest_incurred > 0
             THEN COALESCE(amount_outstanding, 0) + COALESCE(interest_incurred, 0)


             ELSE amount_outstanding
        END,
    updated_at = CURRENT_TIMESTAMP
WHERE
    amount_outstanding IS NOT NULL
  AND amount_outstanding > 0
  AND interest_rate IS NOT NULL;