package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;


import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InviteOrganizationResponse {
    private String id;
    private String createdBy;
    private String email;
    private ServiceOffering serviceOffering;
    private List<ServiceOffering> serviceOfferings;
    private List<OrganizationEmployeeIdentity> organizationEmployees;
}
