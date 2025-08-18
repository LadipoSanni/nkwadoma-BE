package africa.nkwadoma.nkwadoma.domain.enums;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Set;

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
    COOPERATE_FINANCIER_SUPER_ADMIN,
    COOPERATE_FINANCIER_ADMIN,;



    public static Set<IdentityRole> getMeedlRoles(){
        return Set.of(MEEDL_SUPER_ADMIN, MEEDL_ADMIN, MEEDL_ASSOCIATE, PORTFOLIO_MANAGER);
    }
    public static Set<IdentityRole> getOrganizationRoles(){
        return Set.of(ORGANIZATION_ADMIN, ORGANIZATION_ASSOCIATE, ORGANIZATION_SUPER_ADMIN);
    }
    public static boolean isMeedlAdminOrSuperAdmin(UserIdentity actor) {
        return IdentityRole.MEEDL_SUPER_ADMIN.equals(actor.getRole()) ||
                IdentityRole.MEEDL_ADMIN.equals(actor.getRole());
    }

    public static boolean isOrganizationAdminOrSuperAdmin(UserIdentity actor) {
        return IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(actor.getRole()) ||
                IdentityRole.ORGANIZATION_ADMIN.equals(actor.getRole());
    }

    @JsonCreator
    public static IdentityRole fromString(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return IdentityRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid identity role: " + value);
        }
    }

    public static boolean isMeedlStaff(IdentityRole role) {
        return getMeedlRoles().contains(role);
    }

    public static boolean isOrganizationStaff(IdentityRole role) {
        return getOrganizationRoles().contains(role);
    }

    public static boolean isAssignableMeedlRole(IdentityRole role) {
        return Set.of(MEEDL_ADMIN, MEEDL_ASSOCIATE, PORTFOLIO_MANAGER).contains(role);
    }

    public static boolean isAssignableOrganizationRole(IdentityRole role) {
        return Set.of(ORGANIZATION_ADMIN, ORGANIZATION_ASSOCIATE).contains(role);
    }


    public boolean isMeedlRole() {
        return this == MEEDL_SUPER_ADMIN ||
                this == MEEDL_ADMIN ||
                this == MEEDL_ASSOCIATE ||
                this == PORTFOLIO_MANAGER;
    }

}
