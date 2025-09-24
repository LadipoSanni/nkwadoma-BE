-- Step 1: Drop the old check constraint
DO $$
DECLARE
constraint_name text;
BEGIN
SELECT conname INTO constraint_name
FROM pg_constraint
WHERE conrelid = 'meedl_notification_entity'::regclass
      AND contype = 'c'
      AND conname LIKE '%notification_flag%';

IF constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE meedl_notification_entity DROP CONSTRAINT %I', constraint_name);
END IF;
END$$;


-- Step 2: Add the updated check constraint with the new value
ALTER TABLE meedl_notification_entity
    ADD CONSTRAINT meedl_notification_entity_notification_flag_check
        CHECK (notification_flag IN (
                                     'LOAN_OFFER',
                                     'LOAN_OFFER_DECISION',
                                     'LOAN_REQUEST',
                                     'INVITE_FINANCIER',
                                     'REQUESTING_APPROVAL_FINANCIER_INVITATION',
                                     'INVESTMENT_VEHICLE',
                                     'APPROVE_INVITE_ORGANIZATION',
                                     'ORGANIZATION_DEACTIVATED',
                                     'ORGANIZATION_REACTIVATED',
                                     'LOAN_DEFERRAL',
                                     'LOAN_REFERRAL',
                                     'DROP_OUT',
                                     'LOANEE_DATA_UPLOAD_FAILURE',
                                     'LOANEE_DATA_UPLOAD_SUCCESS',
                                     'REPAYMENT_UPLOAD_SUCCESS',
                                     'REPAYMENT_UPLOAD_FAILURE',
                                     'MEEDL_SUPER_ADMIN_DEACTIVATION_ATTEMPT',
                                     'ORGANIZATION_INVITATION_REQUESTING_APPROVAL',
                                     'ORGANIZATION_INVITATION_APPROVED',
                                     'ORGANIZATION_INVITATION_DECLINED',
                                     'INVITE_COLLEAGUE',
                                     'DECLINE_COLLEAGUE_INVITE',
                                     'FINANCIER_INVITATION_RESPONSE', -- new one
                                     'INVITE_COOPERATE_COLLEAGUE_APPROVAL',
                                     'INVITE_COOPERATE_COLLEAGUE_DECLINED'
            ));

ALTER TABLE investment_vehicle_financier_entity
ALTER COLUMN date_invested TYPE TIMESTAMP WITHOUT TIME ZONE
USING date_invested::timestamp;
