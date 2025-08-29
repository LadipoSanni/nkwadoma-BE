ALTER TABLE loan_offer_entity DROP CONSTRAINT IF EXISTS loan_offer_entity_loan_offer_status_check;


ALTER TABLE loan_offer_entity
    ADD CONSTRAINT loan_offer_entity_loan_offer_status_check
        CHECK (loan_offer_status IN ('OFFERED', 'WITHDRAW'));