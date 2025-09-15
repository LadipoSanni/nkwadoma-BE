-- 1. Add the new column (nullable first to avoid breaking existing rows)
ALTER TABLE organization
    ADD COLUMN organization_type VARCHAR(50);

-- 2. Update existing organizations
UPDATE organization
SET organization_type = 'MEEDL'
WHERE LOWER(name) = 'meedl';

UPDATE organization
SET organization_type = 'VALIDATION_PASS'
WHERE LOWER(name) <> 'meedl' OR name IS NULL;

-- 3. Optionally enforce NOT NULL constraint after migration
ALTER TABLE organization
    ALTER COLUMN organization_type SET NOT NULL;
