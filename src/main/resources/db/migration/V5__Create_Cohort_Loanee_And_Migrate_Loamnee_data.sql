-- Creating cohort_loanee table
CREATE TABLE IF NOT EXISTS cohort_loanee_entity (
    id VARCHAR(36) PRIMARY KEY,
    cohort_id VARCHAR(36) NOT NULL,
    loanee_id VARCHAR(36) NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    loanee_loan_detail_id VARCHAR(36),
    loanee_status VARCHAR(50) NOT NULL,
    onboarding_mode VARCHAR(50) NOT NULL,
    uploaded_status VARCHAR(50) NOT NULL,
    referral_date_time TIMESTAMP,
    referred_by VARCHAR(255),
    reason_for_dropout TEXT,
    deferred_date_and_time TIMESTAMP,
    defer_reason TEXT,
    deferral_requested BOOLEAN NOT NULL DEFAULT FALSE,
    deferral_approved BOOLEAN NOT NULL DEFAULT FALSE,
    dropout_requested BOOLEAN NOT NULL DEFAULT FALSE,
    dropout_approved BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (cohort_id) REFERENCES cohort_entity(id),
    FOREIGN KEY (loanee_id) REFERENCES loanee_entity(id),
    FOREIGN KEY (loanee_loan_detail_id) REFERENCES loanee_loan_detail_entity(id)
    );

-- Backfill created_at in loanee_entity from meedl_user only when NULL
UPDATE loanee_entity l
SET created_at = COALESCE(
        TO_TIMESTAMP(u.createdAt, 'YYYY-MM-DD HH24:MI:SS'),
        CURRENT_TIMESTAMP
                 )
    FROM meedl_user u
WHERE l.user_identity_id = u.id
  AND l.created_at IS NULL;

--  Migrating data from loanee to cohort_loanee
INSERT INTO cohort_loanee_entity (
    id,
    cohort_id,
    loanee_id,
    created_by,
    created_at,
    updated_at,
    loanee_loan_detail_id,
    loanee_status,
    onboarding_mode,
    uploaded_status,
    referral_date_time,
    referred_by,
    reason_for_dropout,
    deferred_date_and_time,
    defer_reason,
    deferral_requested,
    deferral_approved,
    dropout_requested,
    dropout_approved
)
SELECT
    l.id,
    l.cohort_id,
    l.id AS loanee_id,
    l.created_by,
    COALESCE(
            l.created_at,
            TO_TIMESTAMP(u.createdAt, 'YYYY-MM-DD HH24:MI:SS'),
            CURRENT_TIMESTAMP
    ) AS created_at,
    l.updated_at,
    l.loanee_loan_detail_id,
    l.loanee_status,
    COALESCE(l.onboarding_mode, 'EMAIL_REFERRED') AS onboarding_mode,
    COALESCE(l.uploaded_status, 'ADDED') AS uploaded_status,
    l.referral_date_time,
    l.referred_by,
    l.reason_for_dropout,
    l.deferred_date_and_time,
    l.defer_reason,
    l.deferral_requested,
    l.deferral_approved,
    l.dropout_requested,
    l.dropout_approved
FROM loanee_entity l
         LEFT JOIN meedl_user u ON l.user_identity_id = u.id
WHERE l.cohort_id IS NOT NULL;