ALTER TABLE cohort_loanee_entity
     ADD COLUMN if not exists employment_status VARCHAR(50) DEFAULT null,
     ADD COLUMN if not exists training_performance VARCHAR(50) DEFAULT null;