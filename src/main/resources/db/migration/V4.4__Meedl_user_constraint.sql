DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'meedl_user_role_check'
        AND conrelid = 'meedl_user'::regclass
    ) THEN
ALTER TABLE meedl_user DROP CONSTRAINT meedl_user_role_check;
END IF;
END $$;

-- Add a new constraint with updated roles
ALTER TABLE meedl_user ADD CONSTRAINT meedl_user_role_check
    CHECK (role IN ('PORTFOLIO_MANAGER', 'ORGANIZATION_ADMIN', 'LOANEE', 'FINANCIER'));
