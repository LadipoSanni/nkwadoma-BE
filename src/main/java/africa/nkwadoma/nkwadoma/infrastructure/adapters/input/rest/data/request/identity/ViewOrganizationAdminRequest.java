package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewOrganizationAdminRequest {
    private String name;
    private ActivationStatus activationStatus;
    private Set <IdentityRole> identityRoles;
    private int pageSize=10;
    private int pageNumber=0;
}
