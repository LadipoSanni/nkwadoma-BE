

-- Create the new collection table if not already created
CREATE TABLE IF NOT EXISTS vendor_provider_services (
                                                        vendor_id VARCHAR(255) NOT NULL,
                                                        provider_service   TEXT NOT NULL,
                                                        CONSTRAINT fk_vendor FOREIGN KEY (vendor_id) REFERENCES vendor_entity(id)
    );

-- Migrate data from product (ordinal int) into vendor_provider_services
INSERT INTO vendor_provider_services (vendor_id, provider_service)
SELECT id,
       CASE product
           WHEN 0 THEN 'CREDIT_LIFE_INSURANCE_PROVIDER'
           WHEN 1 THEN 'HEALTH_INSURANCE_PROVIDER'
           WHEN 2 THEN 'ACCOMMODATION'
           WHEN 3 THEN 'DEVICE'
           END
FROM vendor_entity;

-- Optional: If you don’t want the old product column anymore, you can drop it:
ALTER TABLE vendor_entity DROP COLUMN product;
