ALTER TABLE meedl_notification_entity
DROP COLUMN IF EXISTS sender_email;

UPDATE meedl_notification_entity notification
SET notification_flag = CASE
                            WHEN o.status = 'INVITED' THEN 'ORGANIZATION_INVITATION_REQUESTING_APPROVAL'
                            WHEN o.status = 'ACTIVE' THEN 'ORGANIZATION_INVITATION_APPROVED'
                            ELSE notification.notification_flag
    END
    FROM organization o
WHERE notification.notification_flag = 'INVITE_ORGANIZATION'
  AND notification.content_id = o.id;
