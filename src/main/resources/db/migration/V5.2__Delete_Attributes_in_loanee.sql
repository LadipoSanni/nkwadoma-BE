-- Dropping foreign key constraint for loanee_loan_detail_id
ALTER TABLE loanee_entity
DROP CONSTRAINT IF EXISTS fk_loanee_entity_loanee_loan_detail;

-- Dropping columns for removed attributes
ALTER TABLE loanee_entity
DROP COLUMN IF EXISTS loanee_loan_detail_id,
DROP COLUMN IF EXISTS loanee_status,
DROP COLUMN IF EXISTS cohort_id,
DROP COLUMN IF EXISTS onboarding_mode,
DROP COLUMN IF EXISTS uploaded_status,
DROP COLUMN IF EXISTS referral_date_time,
DROP COLUMN IF EXISTS referred_by,
DROP COLUMN IF EXISTS reason_for_dropout,
DROP COLUMN IF EXISTS deferred_date_and_time,
DROP COLUMN IF EXISTS defer_reason,
DROP COLUMN IF EXISTS deferral_requested,
DROP COLUMN IF EXISTS deferral_approved,
DROP COLUMN IF EXISTS dropout_requested,
DROP COLUMN IF EXISTS dropout_approved,
DROP COLUMN IF EXISTS created_by;