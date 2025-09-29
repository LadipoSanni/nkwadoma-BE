ALTER TABLE vendor_entity
    ADD COLUMN IF NOT EXISTS cost_of_service NUMERIC(19,2);

ALTER TABLE vendor_entity
    ADD COLUMN IF NOT EXISTS duration INT DEFAULT 0;

ALTER TABLE vendor_entity
    ALTER COLUMN cost_of_service TYPE DECIMAL(28,8);

ALTER TABLE daily_interest_entity
    ALTER COLUMN interest TYPE DECIMAL(28,8);


