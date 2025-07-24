ALTER TABLE loan_entity
DROP CONSTRAINT IF EXISTS fk_loan_loanee_entity;

ALTER TABLE loan_entity
DROP COLUMN loanee_entity_id;