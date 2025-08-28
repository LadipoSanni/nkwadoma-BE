-- Drop the existing unique constraint on loan_product_entity_id
ALTER TABLE loan_product_vendor
DROP CONSTRAINT IF EXISTS loan_product_vendor_loan_product_entity_id_key;

-- Drop the existing unique constraint on vendor_entity_id (if any)
ALTER TABLE loan_product_vendor
DROP CONSTRAINT IF EXISTS loan_product_vendor_vendor_entity_id_key;

-- Ensure you have a foreign key (optional, if already missing)
DO $$
BEGIN
    -- FK to loan_product
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_loan_product_vendor_product'
    ) THEN
ALTER TABLE loan_product_vendor
    ADD CONSTRAINT fk_loan_product_vendor_product FOREIGN KEY (loan_product_entity_id)
        REFERENCES loan_product (id);
END IF;

    -- FK to vendor_entity
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_loan_product_vendor_vendor'
    ) THEN
ALTER TABLE loan_product_vendor
    ADD CONSTRAINT fk_loan_product_vendor_vendor FOREIGN KEY (vendor_entity_id)
        REFERENCES vendor_entity (id);
END IF;

    -- Composite unique constraint
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_loan_product_vendor'
    ) THEN
ALTER TABLE loan_product_vendor
    ADD CONSTRAINT uq_loan_product_vendor UNIQUE (loan_product_entity_id, vendor_entity_id);
END IF;
END$$;