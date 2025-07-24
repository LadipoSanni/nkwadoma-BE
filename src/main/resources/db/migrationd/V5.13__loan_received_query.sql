UPDATE loanee_loan_detail_entity lld
SET
    amount_received = (
        SELECT lo.amount_approved
        FROM loan_offer_entity lo
                 JOIN loan_entity l ON l.loan_offer_id = lo.id
                 JOIN loan_request_entity lr on lr.id = lo.id
                 JOIN loan_referral_entity lre on lre.id = lr.id
                 JOIN cohort_loanee_entity cl ON cl.id = lre.cohort_loanee_id
        WHERE lld.id = cl.loanee_loan_detail_id
    ),
    amount_outstanding = CASE
             WHEN COALESCE(lld.amount_received, 0) - COALESCE(lld.amount_repaid, 0) < 0 THEN 0
             ELSE COALESCE(lld.amount_received, 0) - COALESCE(lld.amount_repaid, 0)
    END;