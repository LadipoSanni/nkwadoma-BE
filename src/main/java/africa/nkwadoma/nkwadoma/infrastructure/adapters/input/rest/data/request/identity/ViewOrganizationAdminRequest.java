package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewOrganizationAdminRequest {
    private ActivationStatus status;
    private Set <IdentityRole> identityRoles;
    private int pageSize=10;
    private int pageNumber=0;
}
