ALTER TABLE meedl_user DROP CONSTRAINT meedl_user_role_check;
ALTER TABLE meedl_user ADD CONSTRAINT meedl_user_role_check
    CHECK (role IN ('PORTFOLIO_MANAGER', 'ORGANIZATION_ADMIN', 'LOANEE', 'FINANCIER'));
