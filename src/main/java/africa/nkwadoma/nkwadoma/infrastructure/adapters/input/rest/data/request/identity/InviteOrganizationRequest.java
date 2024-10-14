package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InviteOrganizationRequest {
    private String industry;
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
    private String createdBy;
}
