DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.columns
        WHERE table_name = 'investment_vehicle_entity'
        AND column_name = 'interest_rate_offered'
    ) THEN
ALTER TABLE investment_vehicle_entity
    ADD COLUMN interest_rate_offered FLOAT;
END IF;
END $$;