package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationResponse {
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
    private String createdBy;
//    private List<ServiceOffering> serviceOfferings;
    private String logoImage;
    private String bannerImage;
    private String address;
}
