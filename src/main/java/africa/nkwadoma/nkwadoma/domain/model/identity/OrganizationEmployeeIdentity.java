package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.*;

import java.math.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEmployeeIdentity {
    private String id;
    private UserIdentity meedlUser;
    private String organization;
    private ActivationStatus status;
    private int pageNumber;
    private int pageSize;

    public OrganizationEmployeeIdentity(String organizationId, int pageNumber, int pageSize) {
        organization = organizationId;
        pageNumber = pageNumber;
        pageSize = pageSize;
    }

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return  this.pageSize == BigInteger.ZERO.intValue() ? defaultPageSize : this.pageSize;
    }
}
