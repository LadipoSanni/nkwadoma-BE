-- Fix organization_bank_detail_entity table to match entity model
ALTER TABLE organization_bank_detail_entity
    RENAME COLUMN organization_id TO organization_entity_id;

-- Ensure foreign key is correct
ALTER TABLE organization_bank_detail_entity
DROP CONSTRAINT IF EXISTS organization_bank_detail_entity_organization_id_fkey;

ALTER TABLE organization_bank_detail_entity
    ADD CONSTRAINT fk_org_bank_detail_org FOREIGN KEY (organization_entity_id) REFERENCES organization(id);
