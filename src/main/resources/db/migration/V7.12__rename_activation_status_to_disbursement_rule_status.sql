-- ================================================
-- Rename activation_status to disbursement_rule_status
-- in both loan_disbursement_rule_entity and disbursement_rule_entity tables
-- ================================================

-- 1️⃣ For loan_disbursement_rule_entity
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'loan_disbursement_rule_entity'
          AND column_name = 'activation_status'
    ) THEN
ALTER TABLE loan_disbursement_rule_entity
    RENAME COLUMN activation_status TO disbursement_rule_status;
END IF;
END $$;

-- 2️⃣ For disbursement_rule_entity
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'disbursement_rule_entity'
          AND column_name = 'activation_status'
    ) THEN
ALTER TABLE disbursement_rule_entity
    RENAME COLUMN activation_status TO disbursement_rule_status;
END IF;
END $$;

-- Optional: ensure the new column uses correct enum type if needed
-- (only if you previously had activationstatus enum)
-- ALTER TABLE loan_disbursement_rule_entity
--     ALTER COLUMN disbursement_rule_status TYPE disbursementrulestatus USING disbursement_rule_status::text::disbursementrulestatus;
-- ALTER TABLE disbursement_rule_entity
--     ALTER COLUMN disbursement_rule_status TYPE disbursementrulestatus USING disbursement_rule_status::text::disbursementrulestatus;
