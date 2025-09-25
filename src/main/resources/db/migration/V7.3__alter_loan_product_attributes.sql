BEGIN;

ALTER TABLE loan_product
    ADD COLUMN available_amount_to_be_offered DECIMAL(15,4) NOT NULL DEFAULT 0.00,
    ADD COLUMN available_amount_to_be_disbursed DECIMAL(15,4) NOT NULL DEFAULT 0.00;

UPDATE loan_product
SET available_amount_to_be_offered = total_amount_available,
    available_amount_to_be_disbursed = total_amount_available;

UPDATE loan_product lp
SET available_amount_to_be_offered = available_amount_to_be_offered - (
    SELECT COALESCE(SUM(loanOffer.amount_approved), 0)
    FROM loan_offer_entity loanOffer
    WHERE loanOffer.loan_product_id = lp.id
);


UPDATE loan_product lp
SET available_amount_to_be_offered = available_amount_to_be_offered + (
    SELECT COALESCE(SUM(loanOffer.amount_approved), 0)
    FROM loan_offer_entity loanOffer
    WHERE loanOffer.loan_product_id = lp.id
      AND (loanOffer.loanee_response = 'DECLINED' OR loanOffer.loan_offer_status = 'WITHDRAW')
);

UPDATE loan_product lp
SET available_amount_to_be_disbursed = available_amount_to_be_disbursed - (
    SELECT COALESCE(SUM(loanOffer.amount_approved), 0)
    FROM loan_entity loan
             JOIN loan_offer_entity loanOffer ON loanOffer.id = loan.loan_offer_id
    WHERE loanOffer.loan_product_id = lp.id
);

UPDATE loan_product lp
SET total_amount_available = available_amount_to_be_disbursed;



UPDATE cohort_loanee_entity
SET employment_status = 'UNEMPLOYED'
WHERE employment_status IS NULL;

COMMIT;