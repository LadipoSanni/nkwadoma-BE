package africa.nkwadoma.nkwadoma.domain.enums.identity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Set;

@Getter
public enum IdentityRole {
    PORTFOLIO_MANAGER_ASSOCIATE("Portfolio manager associate"),
    MEEDL_SUPER_ADMIN("Meedl super admin"),
    MEEDL_ADMIN("Meedl admin"),
    PORTFOLIO_MANAGER("Portfolio manager"),
    ORGANIZATION_ADMIN("Organization admin"),
    ORGANIZATION_ASSOCIATE("Organization associate"),
    ORGANIZATION_SUPER_ADMIN("Organization super admin"),
    LOANEE("Loanee"),
    FINANCIER("Financier"),
    COOPERATE_FINANCIER_SUPER_ADMIN("Cooperate financier super admin "),
    COOPERATE_FINANCIER_ADMIN("Cooperate financier admin"),;

    private final String roleName;

    IdentityRole(String roleName) {
        this.roleName = roleName;
    }

    public static Set<IdentityRole> getMeedlRoles(){
        return Set.of(MEEDL_SUPER_ADMIN, MEEDL_ADMIN, PORTFOLIO_MANAGER_ASSOCIATE, PORTFOLIO_MANAGER);
    }
    public static Set<IdentityRole> getOrganizationRoles(){
        return Set.of(ORGANIZATION_ADMIN, ORGANIZATION_ASSOCIATE, ORGANIZATION_SUPER_ADMIN);
    }
    public static Set<IdentityRole> getCooperateFinancierRoles(){
        return Set.of(COOPERATE_FINANCIER_ADMIN, COOPERATE_FINANCIER_SUPER_ADMIN);
    }
    public static boolean isMeedlAdminOrMeedlSuperAdmin(IdentityRole identityRole) {
        return IdentityRole.MEEDL_SUPER_ADMIN.equals(identityRole) ||
                IdentityRole.MEEDL_ADMIN.equals(identityRole);
    }

    public static boolean isOrganizationAdminOrSuperAdmin(IdentityRole role) {
        return IdentityRole.ORGANIZATION_SUPER_ADMIN.equals(role) ||
                IdentityRole.ORGANIZATION_ADMIN.equals(role);
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

    public static boolean isCooperateFinancier(IdentityRole role) {
        return getCooperateFinancierRoles().contains(role);
    }
    public static boolean isAssignableMeedlRole(IdentityRole role) {
        return Set.of(MEEDL_ADMIN, PORTFOLIO_MANAGER_ASSOCIATE, PORTFOLIO_MANAGER).contains(role);
    }

    public static boolean isAssignableOrganizationRole(IdentityRole role) {
        return Set.of(ORGANIZATION_ADMIN, ORGANIZATION_ASSOCIATE).contains(role);
    }


    public boolean isMeedlRole() {
        return this == MEEDL_SUPER_ADMIN ||
                this == MEEDL_ADMIN ||
                this == PORTFOLIO_MANAGER_ASSOCIATE ||
                this == PORTFOLIO_MANAGER;
    }

    public boolean isMeedlSuperAdmin() {
        return this == MEEDL_SUPER_ADMIN;
    }
    public boolean isSuperAdmin() {
        return this == MEEDL_SUPER_ADMIN ||
                this == COOPERATE_FINANCIER_SUPER_ADMIN ||
                this == ORGANIZATION_SUPER_ADMIN;
    }

    public boolean isCooperateStaff(){
        return this == COOPERATE_FINANCIER_SUPER_ADMIN || this == COOPERATE_FINANCIER_ADMIN;
    }


}
