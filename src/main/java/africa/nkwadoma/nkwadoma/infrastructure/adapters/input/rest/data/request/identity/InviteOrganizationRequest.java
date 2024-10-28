package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InviteOrganizationRequest {
    private String name;
    private String email;
    private String websiteAddress;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
    private String adminFirstName;
    private String adminLastName;
    private String adminEmail;
    private IdentityRole adminRole;
    private List<ServiceOffering> serviceOfferings;
}
