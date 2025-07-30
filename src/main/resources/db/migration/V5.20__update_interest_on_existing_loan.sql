-- Update loans
UPDATE loanee_loan_detail_entity llde
SET
    interest_incurred = (
        CASE
            WHEN loan_start_date IS NOT NULL AND updated_at IS NULL
                AND interest_incurred = 0 AND amount_outstanding > 0
                THEN COALESCE(interest_incurred, 0) + (
                amount_outstanding * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - loan_start_date::date)
                )

            WHEN loan_start_date IS NOT NULL AND updated_at IS NOT NULL
                AND amount_outstanding > 0 AND interest_incurred = 0 AND amount_repaid = 0
                THEN COALESCE(interest_incurred, 0) + (
                amount_outstanding * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - loan_start_date::date)
                )

            WHEN updated_at IS NULL AND loan_start_date IS NOT NULL
                AND interest_incurred > 0 AND amount_outstanding > 0 AND amount_repaid > 0
                THEN
                amount_received * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - loan_start_date::date)

            WHEN loan_start_date IS NOT NULL AND updated_at IS NOT NULL
                AND amount_outstanding > 0 AND interest_incurred > 0
                THEN COALESCE(interest_incurred, 0) + (
                amount_outstanding * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - updated_at::date)
                )

            ELSE COALESCE(interest_incurred, 0)
            END
        ),
    amount_outstanding = (
        CASE
            WHEN loan_start_date IS NOT NULL AND updated_at IS NULL
                AND interest_incurred = 0 AND amount_outstanding > 0
                THEN COALESCE(amount_outstanding, 0) + (
                amount_outstanding * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - loan_start_date::date)
                )

            WHEN loan_start_date IS NOT NULL AND updated_at IS NOT NULL
                AND amount_outstanding > 0 AND interest_incurred = 0 AND amount_repaid = 0
                THEN COALESCE(amount_outstanding, 0) + (
                amount_outstanding * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - loan_start_date::date)
                )

            WHEN updated_at IS NULL AND loan_start_date IS NOT NULL
                AND interest_incurred > 0 AND amount_outstanding > 0 AND amount_repaid > 0
                THEN COALESCE(amount_received, 0) - COALESCE(amount_repaid, 0) + (
                amount_received * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - loan_start_date::date)
                )

            WHEN loan_start_date IS NOT NULL AND updated_at IS NOT NULL
                AND amount_outstanding > 0 AND interest_incurred > 0
                THEN COALESCE(amount_outstanding, 0) + (
                amount_outstanding * (interest_rate / 100.0 / 365.0) *
                ((CURRENT_DATE - INTERVAL '1 day')::date - updated_at::date)
                )

            ELSE COALESCE(amount_outstanding, 0)
            END
        ),
    updated_at = CURRENT_TIMESTAMP
WHERE
    amount_outstanding IS NOT NULL
  AND amount_outstanding > 0
  AND interest_rate IS NOT NULL
  AND (updated_at IS NULL OR updated_at::date != CURRENT_DATE);