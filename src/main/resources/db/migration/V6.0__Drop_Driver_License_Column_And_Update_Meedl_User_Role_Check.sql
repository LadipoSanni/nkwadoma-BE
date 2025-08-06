-- Drop the incorrectly named column from the table
ALTER TABLE beneficial_owner_entity
DROP COLUMN IF EXISTS driver_licensetional_id_card;

ALTER TABLE meedl_user
DROP CONSTRAINT IF EXISTS meedl_user_role_check;

ALTER TABLE meedl_user
    ADD CONSTRAINT meedl_user_role_check
        CHECK (role IN (
                        'MEEDL_ASSOCIATE',
                        'MEEDL_SUPER_ADMIN',
                        'MEEDL_ADMIN',
                        'PORTFOLIO_MANAGER',
                        'ORGANIZATION_ADMIN',
                        'ORGANIZATION_ASSOCIATE',
                        'ORGANIZATION_SUPER_ADMIN',
                        'LOANEE',
                        'FINANCIER'
        ));