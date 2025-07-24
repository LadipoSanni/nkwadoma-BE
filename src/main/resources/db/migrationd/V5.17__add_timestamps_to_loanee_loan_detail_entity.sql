ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN loan_start_date TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();
