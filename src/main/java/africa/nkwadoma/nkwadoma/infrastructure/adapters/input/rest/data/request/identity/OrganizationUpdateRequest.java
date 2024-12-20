package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationUpdateRequest {

    private String id;
    private String logoImage;
    private String bannerImage;
    private String address;
    private String websiteAddress;
    private String phoneNumber;
    private String officeAddress;
}
