-- Ensure uuid_generate_v4 is available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Add the new identity column to financier_entity if not already present
ALTER TABLE financier_entity ADD COLUMN IF NOT EXISTS identity VARCHAR;

DO $$
DECLARE
cooperation RECORD;
    coop_financier RECORD;
    super_admin_financier_id VARCHAR;
    organization_id VARCHAR;
    v_user_identity_id VARCHAR;
    org_status VARCHAR;
BEGIN
    -- Loop over each CooperationEntity
FOR cooperation IN SELECT * FROM cooperation_entity LOOP
                                 -- Default status
    org_status := 'INACTIVE';

-- Pick SUPER_ADMIN activation_status and financier_id (if any)
SELECT cfe.activation_status, cfe.financier_id
INTO org_status, super_admin_financier_id
FROM cooperate_financier_entity cfe
         JOIN financier_entity f ON f.id = cfe.financier_id
         JOIN meedl_user u ON u.id = f.user_identity_id
WHERE cfe.cooperate_id = cooperation.id
  AND u.role = 'COOPERATE_FINANCIER_SUPER_ADMIN'
    LIMIT 1;

-- Always insert a fresh OrganizationEntity
organization_id := uuid_generate_v4()::VARCHAR;
INSERT INTO organization (
    id, name, email, website_address, invited_date, registration_number, tax_identity,
    phone_number, activation_status, rc_number, created_by, updated_by, time_updated,
    is_enabled, logo_image, banner_image, address, office_address
)
VALUES (
           organization_id, cooperation.name, cooperation.email, NULL, NULL, NULL, NULL,
           NULL, org_status, NULL, NULL, NULL, NULL,
           TRUE, NULL, NULL, NULL, NULL
       );

-- Loop over each CooperateFinancierEntity
FOR coop_financier IN SELECT * FROM cooperate_financier_entity WHERE cooperate_id = cooperation.id LOOP
-- Get user_identity_id safely (avoid ambiguity)
SELECT f.user_identity_id
INTO v_user_identity_id
FROM financier_entity f
WHERE f.id = coop_financier.financier_id;

-- Create OrganizationEmployeeEntity
INSERT INTO organization_employee (
    id, organization, activation_status, meedl_user_id, created_by
)
VALUES (
           uuid_generate_v4()::VARCHAR, organization_id, coop_financier.activation_status, v_user_identity_id, NULL
       );

-- Update FinancierEntity identity to organization id
UPDATE financier_entity
SET identity = organization_id
WHERE id = coop_financier.financier_id;
END LOOP;

        -- 🔥 Instead of deleting here, just mark them for cleanup later
        -- We’ll drop cooperate_financier_entity after migration anyway
END LOOP;
END $$;

-- ✅ Now drop the old tables (removes FKs and references)
DROP TABLE IF EXISTS cooperate_financier_entity;
DROP TABLE IF EXISTS cooperation_entity;

-- ✅ After dropping cooperate_financier_entity, it's safe to clean financiers
DELETE FROM financier_entity
WHERE id NOT IN (
    SELECT fe.id FROM financier_entity fe
        JOIN meedl_user u ON u.id = fe.user_identity_id
    WHERE u.role = 'COOPERATE_FINANCIER_SUPER_ADMIN'
);

-- Drop user_identity_id column from financier_entity
ALTER TABLE financier_entity
DROP COLUMN IF EXISTS user_identity_id;
