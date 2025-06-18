-- Disable triggers
ALTER TABLE vehicle_closure_entity DISABLE TRIGGER ALL;
ALTER TABLE vehicle_operation_entity DISABLE TRIGGER ALL;

-- Drop existing check constraints
DO $$
BEGIN
    PERFORM 1 FROM pg_constraint WHERE conname = 'check_recollection_status';
    IF FOUND THEN
        EXECUTE 'ALTER TABLE vehicle_closure_entity DROP CONSTRAINT check_recollection_status';
END IF;
END $$;

DO $$
DECLARE
r RECORD;
BEGIN
FOR r IN
SELECT conname
FROM pg_constraint
WHERE conrelid = 'vehicle_operation_entity'::regclass
          AND contype = 'c'
          AND conname IN (
              'check_coupon_distribution_status',
              'check_deploying_status',
              'check_fund_raising_status',
              'check_operation_status'
          )
    LOOP
        EXECUTE format('ALTER TABLE vehicle_operation_entity DROP CONSTRAINT %I', r.conname);
END LOOP;
END $$;


-- Convert status columns to VARCHAR
DO $$
BEGIN
    -- vehicle_closure_entity.recollection_status
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'vehicle_closure_entity'
          AND column_name = 'recollection_status'
    ) THEN
ALTER TABLE vehicle_closure_entity ADD COLUMN temp_recollection_status VARCHAR(255);

UPDATE vehicle_closure_entity
SET temp_recollection_status = CASE
                                   WHEN recollection_status::VARCHAR = '0' THEN 'OPEN'
            WHEN recollection_status::VARCHAR = '1' THEN 'CLOSE'
            ELSE recollection_status::VARCHAR
END;

ALTER TABLE vehicle_closure_entity DROP COLUMN recollection_status;
ALTER TABLE vehicle_closure_entity RENAME COLUMN temp_recollection_status TO recollection_status;
END IF;

    -- vehicle_operation_entity.operation_status
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'vehicle_operation_entity'
          AND column_name = 'operation_status'
    ) THEN
ALTER TABLE vehicle_operation_entity ADD COLUMN temp_operation_status VARCHAR(255);

UPDATE vehicle_operation_entity
SET temp_operation_status = CASE
                                WHEN operation_status::VARCHAR = '0' THEN 'ACTIVE'
            WHEN operation_status::VARCHAR = '1' THEN 'INACTIVE'
            ELSE operation_status::VARCHAR
END;

ALTER TABLE vehicle_operation_entity DROP COLUMN operation_status;
ALTER TABLE vehicle_operation_entity RENAME COLUMN temp_operation_status TO operation_status;
END IF;

    -- Same pattern for other status columns (coupon_distribution_status, deploying_status, fund_raising_status)...

END $$;

-- Re-enable triggers
ALTER TABLE vehicle_closure_entity ENABLE TRIGGER ALL;
ALTER TABLE vehicle_operation_entity ENABLE TRIGGER ALL;
