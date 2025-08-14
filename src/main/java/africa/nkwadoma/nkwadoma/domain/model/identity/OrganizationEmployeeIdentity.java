package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.*;

import java.math.*;
import java.util.Set;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEmployeeIdentity {
    private String id;
    private String userId;
    private String name;
    private String requestedBy;
    private String createdBy;
    private String organization;
    private ActivationStatus activationStatus;
    private Set<IdentityRole> identityRoles;
    private Set<ActivationStatus> activationStatuses;
    private UserIdentity meedlUser;
    private int pageNumber;
    private int pageSize;

    public OrganizationEmployeeIdentity(String organizationId, int pageNumber, int pageSize) {
        this.organization = organizationId;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == BigInteger.ZERO.intValue() ? defaultPageSize : this.pageSize;
    }
}
