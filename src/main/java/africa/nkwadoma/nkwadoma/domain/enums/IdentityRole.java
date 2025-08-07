package africa.nkwadoma.nkwadoma.domain.enums;

public enum IdentityRole {
    MEEDL_ASSOCIATE,
    MEEDL_SUPER_ADMIN,
    MEEDL_ADMIN,
    PORTFOLIO_MANAGER,
    ORGANIZATION_ADMIN,
    ORGANIZATION_ASSOCIATE,
    ORGANIZATION_SUPER_ADMIN,
    LOANEE,
    FINANCIER,
    ;

    public boolean isMeedlRole() {
        return this == MEEDL_SUPER_ADMIN ||
                this == MEEDL_ADMIN ||
                this == MEEDL_ASSOCIATE ||
                this == PORTFOLIO_MANAGER;
    }
}
