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
