package africa.nkwadoma.nkwadoma.domain.model.identity;

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
    private UserIdentity middlUser;
    private String organization;
    private int pageNumber;
    private int pageSize;

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return  this.pageSize == 0 ? defaultPageSize : this.pageSize;
    }
}
