ALTER TABLE loan_referral_entity
    ADD CONSTRAINT uk_loan_referral_cohort_loanee UNIQUE (cohort_loanee_id);

ALTER TABLE cohort_loanee_entity
DROP CONSTRAINT IF EXISTS uk_cohort_loanee_cohort,
    ADD CONSTRAINT uk_cohort_loanee_loanee_cohort UNIQUE (loanee_id, cohort_id),
    ADD CONSTRAINT uk_cohort_loanee_loanee_loan_detail UNIQUE (loanee_loan_detail_id);