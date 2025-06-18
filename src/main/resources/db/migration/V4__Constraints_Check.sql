-- Disable triggers to prevent interference during schema changes
ALTER TABLE vehicle_closure_entity DISABLE TRIGGER ALL;
ALTER TABLE vehicle_operation_entity DISABLE TRIGGER ALL;

-- Drop any existing check constraints that may reference the columns
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_closure_entity'::regclass
        AND conname = 'check_recollection_status'
        AND contype = 'c'
    ) THEN
        EXECUTE 'ALTER TABLE vehicle_closure_entity DROP CONSTRAINT check_recollection_status';
END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_coupon_distribution_status'
        AND contype = 'c'
    ) THEN
        EXECUTE 'ALTER TABLE vehicle_operation_entity DROP CONSTRAINT check_coupon_distribution_status';
END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_deploying_status'
        AND contype = 'c'
    ) THEN
        EXECUTE 'ALTER TABLE vehicle_operation_entity DROP CONSTRAINT check_deploying_status';
END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_fund_raising_status'
        AND contype = 'c'
    ) THEN
        EXECUTE 'ALTER TABLE vehicle_operation_entity DROP CONSTRAINT check_fund_raising_status';
END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_operation_status'
        AND contype = 'c'
    ) THEN
        EXECUTE 'ALTER TABLE vehicle_operation_entity DROP CONSTRAINT check_operation_status';
END IF;
END $$;

-- Update vehicle_closure_entity: recollection_status if it's INTEGER
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
        AND table_name = 'vehicle_closure_entity'
        AND column_name = 'recollection_status'
        AND data_type = 'integer'
    ) THEN
ALTER TABLE vehicle_closure_entity
    ADD COLUMN temp_recollection_status VARCHAR(255);

UPDATE vehicle_closure_entity
SET temp_recollection_status = CASE
                                   WHEN recollection_status = 0 THEN 'OPEN'
                                   WHEN recollection_status = 1 THEN 'CLOSE'
                                   ELSE recollection_status::VARCHAR
END;

ALTER TABLE vehicle_closure_entity
DROP COLUMN recollection_status;

ALTER TABLE vehicle_closure_entity
    RENAME COLUMN temp_recollection_status TO recollection_status;
END IF;
END $$;

-- Update vehicle_operation_entity: coupon_distribution_status if it's INTEGER
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
        AND table_name = 'vehicle_operation_entity'
        AND column_name = 'coupon_distribution_status'
        AND data_type = 'integer'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD COLUMN temp_coupon_distribution_status VARCHAR(255);

UPDATE vehicle_operation_entity
SET temp_coupon_distribution_status = CASE
                                          WHEN coupon_distribution_status = 0 THEN 'DEFAULT'
                                          WHEN coupon_distribution_status = 1 THEN 'PERFORMING'
                                          WHEN coupon_distribution_status = 2 THEN 'DUE'
                                          WHEN coupon_distribution_status = 3 THEN 'PAID'
                                          ELSE coupon_distribution_status::VARCHAR
END;

ALTER TABLE vehicle_operation_entity
DROP COLUMN coupon_distribution_status;

ALTER TABLE vehicle_operation_entity
    RENAME COLUMN temp_coupon_distribution_status TO coupon_distribution_status;
END IF;
END $$;

-- Update vehicle_operation_entity: deploying_status if it's INTEGER
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
        AND table_name = 'vehicle_operation_entity'
        AND column_name = 'deploying_status'
        AND data_type = 'integer'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD COLUMN temp_deploying_status VARCHAR(255);

UPDATE vehicle_operation_entity
SET temp_deploying_status = CASE
                                WHEN deploying_status = 0 THEN 'OPEN'
                                WHEN deploying_status = 1 THEN 'CLOSE'
                                ELSE deploying_status::VARCHAR
END;

ALTER TABLE vehicle_operation_entity
DROP COLUMN deploying_status;

ALTER TABLE vehicle_operation_entity
    RENAME COLUMN temp_deploying_status TO deploying_status;
END IF;
END $$;

-- Update vehicle_operation_entity: fund_raising_status if it's INTEGER
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
        AND table_name = 'vehicle_operation_entity'
        AND column_name = 'fund_raising_status'
        AND data_type = 'integer'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD COLUMN temp_fund_raising_status VARCHAR(255);

UPDATE vehicle_operation_entity
SET temp_fund_raising_status = CASE
                                   WHEN fund_raising_status = 0 THEN 'OPEN'
                                   WHEN fund_raising_status = 1 THEN 'CLOSE'
                                   ELSE fund_raising_status::VARCHAR
END;

ALTER TABLE vehicle_operation_entity
DROP COLUMN fund_raising_status;

ALTER TABLE vehicle_operation_entity
    RENAME COLUMN temp_fund_raising_status TO fund_raising_status;
END IF;
END $$;

-- Update vehicle_operation_entity: operation_status if it's INTEGER
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
        AND table_name = 'vehicle_operation_entity'
        AND column_name = 'operation_status'
        AND data_type = 'integer'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD COLUMN temp_operation_status VARCHAR(255);

UPDATE vehicle_operation_entity
SET temp_operation_status = CASE
                                WHEN operation_status = 0 THEN 'ACTIVE'
                                WHEN operation_status = 1 THEN 'INACTIVE'
                                ELSE operation_status::VARCHAR
END;

ALTER TABLE vehicle_operation_entity
DROP COLUMN operation_status;

ALTER TABLE vehicle_operation_entity
    RENAME COLUMN temp_operation_status TO operation_status;
END IF;
END $$;

-- Add check constraints for valid string values if not already present
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_closure_entity'::regclass
        AND conname = 'check_recollection_status'
        AND contype = 'c'
    ) THEN
ALTER TABLE vehicle_closure_entity
    ADD CONSTRAINT check_recollection_status
        CHECK (recollection_status IN ('OPEN', 'CLOSE'));
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_coupon_distribution_status'
        AND contype = 'c'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_coupon_distribution_status
        CHECK (coupon_distribution_status IN ('DEFAULT', 'PERFORMING', 'DUE', 'PAID'));
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_deploying_status'
        AND contype = 'c'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_deploying_status
        CHECK (deploying_status IN ('OPEN', 'CLOSE'));
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_fund_raising_status'
        AND contype = 'c'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_fund_raising_status
        CHECK (fund_raising_status IN ('OPEN', 'CLOSE'));
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
        AND conrelid = 'vehicle_operation_entity'::regclass
        AND conname = 'check_operation_status'
        AND contype = 'c'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_operation_status
        CHECK (operation_status IN ('ACTIVE', 'INACTIVE'));
END IF;
END $$;

-- Handle investment_vehicle_financier_entity_investment_vehicle_designation table
-- Drop existing table and constraints if they exist to avoid conflicts
DO $$
BEGIN
    -- Drop foreign key constraint if the table exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = 'inv_veh_financier_designation'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE connamespace = 'public'::regnamespace
            AND conrelid = 'inv_veh_financier_designation'::regclass
            AND conname = 'fk_inv_veh_financier_designation'
            AND contype = 'f'
        ) THEN
            EXECUTE 'ALTER TABLE inv_veh_financier_designation DROP CONSTRAINT fk_inv_veh_financier_designation';
END IF;

        -- Drop table
EXECUTE 'DROP TABLE inv_veh_financier_designation CASCADE';
END IF;

    -- Drop old table with long name if it exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = 'investment_vehicle_financier_entity_investment_vehicle_designation'
    ) THEN
        EXECUTE 'DROP TABLE investment_vehicle_financier_entity_investment_vehicle_designation CASCADE';
END IF;
END $$;

-- Create new table with shortened name
CREATE TABLE inv_veh_financier_designation (
                                               investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
                                               investment_vehicle_designation VARCHAR(255),
                                               CONSTRAINT check_inv_veh_designation
                                                   CHECK (investment_vehicle_designation IN ('DONOR', 'ENDOWER', 'INVESTOR', 'SPONSOR', 'LEAD'))
);

-- Add foreign key constraint
ALTER TABLE inv_veh_financier_designation
    ADD CONSTRAINT fk_inv_veh_financier_designation
        FOREIGN KEY (investment_vehicle_financier_entity_id)
            REFERENCES investment_vehicle_financier_entity (id);

-- Re-enable triggers
ALTER TABLE vehicle_closure_entity ENABLE TRIGGER ALL;
ALTER TABLE vehicle_operation_entity ENABLE TRIGGER ALL;