ALTER TABLE organization
    ADD COLUMN requested_invitation_date TIMESTAMP;

UPDATE organization
SET requested_invitation_date =
        CASE
            WHEN UPPER(activation_status) = 'PENDING_APPROVAL'
                THEN NOW()
            ELSE invited_date
            END;

ALTER TABLE organization
    ALTER COLUMN requested_invitation_date SET NOT NULL;
