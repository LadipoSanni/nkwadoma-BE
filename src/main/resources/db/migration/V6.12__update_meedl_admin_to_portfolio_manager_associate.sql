-- 1. Drop the existing check constraint
ALTER TABLE meedl_user
DROP CONSTRAINT meedl_user_role_check;

-- 2. Update all old roles to the new role
UPDATE meedl_user
SET role = 'PORTFOLIO_MANAGER_ASSOCIATE'
WHERE role = 'MEEDL_ASSOCIATE';

-- 3. Add the final constraint (without old role)
ALTER TABLE meedl_user
    ADD CONSTRAINT meedl_user_role_check
        CHECK (role IN (
                        'PORTFOLIO_MANAGER_ASSOCIATE',
                        'MEEDL_SUPER_ADMIN',
                        'MEEDL_ADMIN',
                        'PORTFOLIO_MANAGER',
                        'ORGANIZATION_ADMIN',
                        'ORGANIZATION_ASSOCIATE',
                        'ORGANIZATION_SUPER_ADMIN',
                        'LOANEE',
                        'FINANCIER',
                        'COOPERATE_FINANCIER_ADMIN',
                        'COOPERATE_FINANCIER_SUPER_ADMIN'
            ));


-- 1. Add the new column as varchar (EnumType.STRING in JPA stores as text)
ALTER TABLE bank_detail_entity
    ADD COLUMN IF NOT EXISTS activation_status VARCHAR(50);

-- 2. Optionally set default value for existing rows
UPDATE bank_detail_entity
SET activation_status = 'PENDING_APPROVAL'
WHERE activation_status IS NULL;
