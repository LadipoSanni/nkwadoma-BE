-- Add check constraint for recollection_status
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'vehicle_closure_entity'::regclass
          AND conname = 'check_recollection_status'
    ) THEN
        ALTER TABLE vehicle_closure_entity
        ADD CONSTRAINT check_recollection_status
            CHECK (recollection_status IN ('OPEN', 'CLOSE'));
    END IF;
END $$;

-- Add check constraints for vehicle_operation_entity
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'vehicle_operation_entity'::regclass
          AND conname = 'check_operation_status'
    ) THEN
        ALTER TABLE vehicle_operation_entity
        ADD CONSTRAINT check_operation_status
            CHECK (operation_status IN ('ACTIVE', 'INACTIVE'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'vehicle_operation_entity'::regclass
          AND conname = 'check_coupon_distribution_status'
    ) THEN
        ALTER TABLE vehicle_operation_entity
        ADD CONSTRAINT check_coupon_distribution_status
            CHECK (coupon_distribution_status IN ('DEFAULT', 'PERFORMING', 'DUE', 'PAID'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'vehicle_operation_entity'::regclass
          AND conname = 'check_deploying_status'
    ) THEN
        ALTER TABLE vehicle_operation_entity
        ADD CONSTRAINT check_deploying_status
            CHECK (deploying_status IN ('OPEN', 'CLOSE'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'vehicle_operation_entity'::regclass
          AND conname = 'check_fund_raising_status'
    ) THEN
        ALTER TABLE vehicle_operation_entity
        ADD CONSTRAINT check_fund_raising_status
            CHECK (fund_raising_status IN ('OPEN', 'CLOSE'));
    END IF;
END $$;
