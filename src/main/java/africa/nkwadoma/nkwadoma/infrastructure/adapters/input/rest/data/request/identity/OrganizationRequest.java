package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequest {
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    @Pattern(regexp = MeedlPatterns.RC_NUMBER_REGEX_PATTERN, message = ErrorMessages.INVALID_RC_NUMBER)
    private String rcNumber;
    @Pattern(regexp = MeedlPatterns.TIN_REGEX_PATTERN, message = ErrorMessages.INVALID_TIN)
    private String tin;
    private String phoneNumber;
    private String createdBy;
    private List<ServiceOffering> serviceOfferings;
    private String logoImage;
    private String bannerImage;
    private String address;

    private String adminFirstName;
    private String adminLastName;
    private String adminEmail;
    private IdentityRole adminRole;


}
