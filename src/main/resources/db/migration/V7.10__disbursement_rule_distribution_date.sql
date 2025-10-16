-- 1️⃣ Remove old column (if it exists)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'start_date'
    ) THEN
ALTER TABLE disbursement_rule_entity
DROP COLUMN start_date;
END IF;
END $$;

-- 2️⃣ Rename end_date to date_updated (if end_date exists and date_updated doesn’t)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'end_date'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'date_updated'
    ) THEN
ALTER TABLE disbursement_rule_entity
    RENAME COLUMN end_date TO date_updated;
END IF;
END $$;

-- 3️⃣ Create new table for distribution_dates (List<LocalDateTime>)
CREATE TABLE IF NOT EXISTS disbursement_rule_entity_distribution_dates (
                                                                           disbursement_rule_entity_id VARCHAR(255) NOT NULL,
    distribution_dates TIMESTAMP,
    CONSTRAINT fk_disbursement_rule_distribution_dates
    FOREIGN KEY (disbursement_rule_entity_id)
    REFERENCES disbursement_rule_entity(id)
    ON DELETE CASCADE
    );

-- 1️⃣ Add number_of_times_applied if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'number_of_times_applied'
    ) THEN
ALTER TABLE disbursement_rule_entity
    ADD COLUMN number_of_times_applied INT DEFAULT 0;
END IF;
END $$;

-- 2️⃣ Add number_of_times_adjusted if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'number_of_times_adjusted'
    ) THEN
ALTER TABLE disbursement_rule_entity
    ADD COLUMN number_of_times_adjusted INT DEFAULT 0;
END IF;
END $$;

-- 3️⃣ Add number_of_usage if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'number_of_usage'
    ) THEN
ALTER TABLE disbursement_rule_entity
    ADD COLUMN number_of_usage INT DEFAULT 0;
END IF;
END $$;
