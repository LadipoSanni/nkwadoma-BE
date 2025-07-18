ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN interest_rate DOUBLE PRECISION DEFAULT 0.0;

ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN interest_incurred DOUBLE PRECISION DEFAULT 0.0;
