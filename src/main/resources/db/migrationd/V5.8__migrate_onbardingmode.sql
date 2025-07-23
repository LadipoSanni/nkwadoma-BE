ALTER TABLE loanee_entity
    ADD COLUMN onboarding_mode VARCHAR,
ADD COLUMN uploaded_status VARCHAR;


UPDATE loanee_entity
SET onboarding_mode = cle.onboarding_mode,
    uploaded_status = cle.uploaded_status
    FROM (
    SELECT loanee_id, onboarding_mode, uploaded_status
    FROM cohort_loanee_entity
    WHERE (loanee_id, created_at) IN (
        SELECT loanee_id, MAX(created_at)
        FROM cohort_loanee_entity
        GROUP BY loanee_id
    )
) cle
WHERE cle.loanee_id = loanee_entity.id;

ALTER TABLE cohort_loanee_entity
DROP COLUMN onboarding_mode,
DROP COLUMN uploaded_status;
