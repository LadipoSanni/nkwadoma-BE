-- Disable triggers to prevent interference during schema changes
ALTER TABLE vehicle_closure_entity DISABLE TRIGGER ALL;
ALTER TABLE vehicle_operation_entity DISABLE TRIGGER ALL;

-- Drop user_id column from next_of_kin_entity if it exists (field was removed from entity)
ALTER TABLE next_of_kin_entity DROP COLUMN IF EXISTS user_id;

-- Drop any existing check constraints
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE connamespace = 'public'::regnamespace
          AND conrelid = 'vehicle_closure_entity'::regclass
          AND conname = 'check_recollection_status'
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

-- Convert recollection_status
DO $$
DECLARE col_type TEXT;
BEGIN
SELECT data_type INTO col_type FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'vehicle_closure_entity' AND column_name = 'recollection_status';

IF col_type IN ('integer', 'smallint') THEN
ALTER TABLE vehicle_closure_entity ADD COLUMN temp_recollection_status VARCHAR(255);
UPDATE vehicle_closure_entity SET temp_recollection_status = CASE
                                                                 WHEN recollection_status = 0 THEN 'OPEN'
                                                                 WHEN recollection_status = 1 THEN 'CLOSE'
                                                                 ELSE recollection_status::VARCHAR END;
ALTER TABLE vehicle_closure_entity DROP COLUMN recollection_status;
ALTER TABLE vehicle_closure_entity RENAME COLUMN temp_recollection_status TO recollection_status;
END IF;
END $$;

-- Convert status columns in vehicle_operation_entity
DO $$
DECLARE col TEXT; val TEXT;
BEGIN
FOR col, val IN SELECT unnest(ARRAY[
                                  'coupon_distribution_status', 'deploying_status', 'fund_raising_status', 'operation_status'
                                  ]), unnest(ARRAY[
                                                 'DEFAULT:0,PERFORMING:1,DUE:2,PAID:3',
                                             'OPEN:0,CLOSE:1',
                                             'OPEN:0,CLOSE:1',
                                             'ACTIVE:0,INACTIVE:1'
                                                 ]) LOOP

                    IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'vehicle_operation_entity'
              AND column_name = col
              AND data_type IN ('integer', 'smallint')
        ) THEN
            EXECUTE format('ALTER TABLE vehicle_operation_entity ADD COLUMN temp_%I VARCHAR(255)', col);

EXECUTE format('UPDATE vehicle_operation_entity SET temp_%I = CASE ', col) ||
        string_agg(
                format('WHEN %I = %s THEN ''%s''', col, split_part(pair, ':', 2), split_part(pair, ':', 1)),
                ' '
        ) ||
        format(' ELSE %I::VARCHAR END', col)
    FROM regexp_split_to_table(val, ',') pair;

EXECUTE format('ALTER TABLE vehicle_operation_entity DROP COLUMN %I', col);
EXECUTE format('ALTER TABLE vehicle_operation_entity RENAME COLUMN temp_%I TO %I', col, col);
END IF;
END LOOP;
END $$;

-- Add check constraints back
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'check_recollection_status'
    ) THEN
ALTER TABLE vehicle_closure_entity
    ADD CONSTRAINT check_recollection_status
        CHECK (recollection_status IN ('OPEN', 'CLOSE'));
END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'check_coupon_distribution_status'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_coupon_distribution_status
        CHECK (coupon_distribution_status IN ('DEFAULT', 'PERFORMING', 'DUE', 'PAID'));
END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'check_deploying_status'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_deploying_status
        CHECK (deploying_status IN ('OPEN', 'CLOSE'));
END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'check_fund_raising_status'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_fund_raising_status
        CHECK (fund_raising_status IN ('OPEN', 'CLOSE'));
END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'check_operation_status'
    ) THEN
ALTER TABLE vehicle_operation_entity
    ADD CONSTRAINT check_operation_status
        CHECK (operation_status IN ('ACTIVE', 'INACTIVE'));
END IF;
END $$;

-- Rebuild junction table with short name
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'inv_veh_financier_designation') THEN
        IF EXISTS (
            SELECT 1 FROM pg_constraint
            WHERE conname = 'fk_inv_veh_financier_designation'
        ) THEN
ALTER TABLE inv_veh_financier_designation DROP CONSTRAINT fk_inv_veh_financier_designation;
END IF;
DROP TABLE inv_veh_financier_designation CASCADE;
END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables
               WHERE table_name = 'investment_vehicle_financier_entity_investment_vehicle_designation') THEN
DROP TABLE investment_vehicle_financier_entity_investment_vehicle_designation CASCADE;
END IF;
END $$;

CREATE TABLE inv_veh_financier_designation (
                                               investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
                                               investment_vehicle_designation VARCHAR(255),
                                               CONSTRAINT check_inv_veh_designation CHECK (
                                                   investment_vehicle_designation IN ('DONOR', 'ENDOWER', 'INVESTOR', 'SPONSOR', 'LEAD')
                                                   )
);

ALTER TABLE inv_veh_financier_designation
    ADD CONSTRAINT fk_inv_veh_financier_designation
        FOREIGN KEY (investment_vehicle_financier_entity_id)
            REFERENCES investment_vehicle_financier_entity (id);

-- Re-enable triggers
ALTER TABLE vehicle_closure_entity ENABLE TRIGGER ALL;
ALTER TABLE vehicle_operation_entity ENABLE TRIGGER ALL;