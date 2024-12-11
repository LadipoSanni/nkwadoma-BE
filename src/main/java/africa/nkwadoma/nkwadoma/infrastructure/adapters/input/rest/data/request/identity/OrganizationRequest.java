package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

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
    @Pattern(regexp = "^RC\\d{7}$", message = "Registration number must start with 'RC' followed by exactly 7 digits.")
    private String rcNumber;
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
