UPDATE organization_loan_detail_entity old
SET
    total_outstanding_amount =
        CASE
            WHEN COALESCE(old.total_amount_received, 0) - COALESCE(old.total_amount_repaid, 0) < 0 THEN 0
            ELSE COALESCE(old.total_amount_received, 0) - COALESCE(old.total_amount_repaid, 0)
            END;


UPDATE program_loan_detail_entity pld
SET
    total_outstanding_amount =
        CASE
            WHEN COALESCE(pld.total_amount_received, 0) - COALESCE(pld.total_amount_repaid, 0) < 0 THEN 0
            ELSE COALESCE(pld.total_amount_received, 0) - COALESCE(pld.total_amount_repaid, 0)
            END;

UPDATE cohort_loan_detail_entity cld
SET
    total_outstanding_amount =
        CASE
            WHEN COALESCE(cld.total_amount_received, 0) - COALESCE(cld.total_amount_repaid, 0) < 0 THEN 0
            ELSE COALESCE(cld.total_amount_received, 0) - COALESCE(cld.total_amount_repaid, 0)
            END;



UPDATE loanee_loan_detail_entity lld
SET
    amount_received = COALESCE((
       SELECT lo.amount_approved
       FROM loan_offer_entity lo
                JOIN loan_entity l ON l.loan_offer_id = lo.id
                JOIN loan_request_entity lr ON lr.id = lo.id
                JOIN loan_referral_entity lre ON lre.id = lr.id
                JOIN cohort_loanee_entity cl ON cl.id = lre.cohort_loanee_id
       WHERE lld.id = cl.loanee_loan_detail_id
   ), 0),
    amount_outstanding = CASE
                             WHEN COALESCE(lld.amount_received, 0) - COALESCE(lld.amount_repaid, 0) < 0 THEN 0
                             ELSE COALESCE(lld.amount_received, 0) - COALESCE(lld.amount_repaid, 0)
    END;


