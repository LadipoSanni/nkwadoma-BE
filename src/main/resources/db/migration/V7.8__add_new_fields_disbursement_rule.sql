-- Add new columns to disbursement_rule_entity
ALTER TABLE disbursement_rule_entity
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255),
    ADD COLUMN IF NOT EXISTS interval VARCHAR(70),
    ADD COLUMN IF NOT EXISTS start_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS end_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS number_of_usage INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS date_created TIMESTAMP;

-- Create a separate table for the percentage_distribution list (since List<Double> requires element collection)
CREATE TABLE IF NOT EXISTS disbursement_rule_entity_percentage_distribution (
                                disbursement_rule_entity_id VARCHAR(255) NOT NULL,
                                percentage_distribution DOUBLE PRECISION,
                                CONSTRAINT fk_disbursement_rule_entity
                                FOREIGN KEY (disbursement_rule_entity_id)
    REFERENCES disbursement_rule_entity(id)
    ON DELETE CASCADE
    );

ALTER TABLE disbursement_rule_entity
DROP COLUMN query;

UPDATE vendor_entity
SET duration = 0
WHERE duration IS NULL;

-- Optional: prevent future nulls
ALTER TABLE vendor_entity
    ALTER COLUMN duration SET DEFAULT 0;