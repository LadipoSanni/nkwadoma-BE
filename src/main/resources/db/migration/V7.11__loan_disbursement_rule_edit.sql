-- ============================================
-- ✅ Safe Migration for LoanDisbursementRuleEntity
-- ============================================

-- 1️⃣ Drop old columns if they exist
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'loan_disbursement_rule_entity'
          AND column_name = 'start_date'
    ) THEN
ALTER TABLE loan_disbursement_rule_entity DROP COLUMN start_date;
END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'loan_disbursement_rule_entity'
          AND column_name = 'end_date'
    ) THEN
ALTER TABLE loan_disbursement_rule_entity DROP COLUMN end_date;
END IF;
END $$;

-- 2️⃣ Add new numeric and timestamp columns (if not already present)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'loan_disbursement_rule_entity'
          AND column_name = 'number_of_times_adjusted'
    ) THEN
ALTER TABLE loan_disbursement_rule_entity
    ADD COLUMN number_of_times_adjusted INT DEFAULT 0;
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'loan_disbursement_rule_entity'
          AND column_name = 'date_last_adjusted'
    ) THEN
ALTER TABLE loan_disbursement_rule_entity
    ADD COLUMN date_last_adjusted TIMESTAMP;
END IF;
END $$;

-- 3️⃣ Create a new table for the element collection `distributionDates`
CREATE TABLE IF NOT EXISTS loan_disbursement_rule_distribution_dates (
                                                                         loan_disbursement_rule_entity_id VARCHAR(255) NOT NULL,
    distribution_dates TIMESTAMP,
    CONSTRAINT fk_loan_disbursement_rule_distribution_dates
    FOREIGN KEY (loan_disbursement_rule_entity_id)
    REFERENCES loan_disbursement_rule_entity(id)
    ON DELETE CASCADE
    );
