DO $$
BEGIN
    IF EXISTS (
        SELECT FROM pg_constraint
        WHERE conname = 'fkixcyfka2uaf4ahgox5aq4898b'
        AND conrelid = 'investment_vehicle_financier_entity_investment_vehicle_designat'::regclass
    ) THEN
ALTER TABLE investment_vehicle_financier_entity_investment_vehicle_designat
DROP CONSTRAINT fkixcyfka2uaf4ahgox5aq4898b;
END IF;
END $$;

DROP TABLE IF EXISTS investment_vehicle_financier_entity_investment_vehicle_designat;