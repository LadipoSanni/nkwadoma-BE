DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'organization'
          AND column_name = 'status'
    ) THEN
ALTER TABLE organization RENAME COLUMN status TO activation_status;
END IF;
END
$$;

-- 1️⃣ Add the new column
ALTER TABLE organization_employee
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);

-- 2️⃣ Update existing employees' created_by based on logic
UPDATE organization_employee oe
SET created_by =
        CASE
            -- If the meedl_user is a MEEDL_SUPER_ADMIN, set created_by to their own id
            WHEN mu.role = 'MEEDL_SUPER_ADMIN' THEN mu.id
            -- Otherwise, set created_by to whatever is in the meedl_user.created_by
            ELSE mu.created_by
            END
    FROM meedl_user mu
WHERE oe.meedl_user_id = mu.id;
