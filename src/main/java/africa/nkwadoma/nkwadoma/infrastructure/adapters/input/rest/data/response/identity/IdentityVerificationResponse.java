package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityVerificationEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityVerificationResponse {

    private String identityId;
    private String bvn;
    private String nin;
    private String email;
    private String identityImage;
    private String token;
    private IdentityVerificationEnum typeOfIdentity;

    private String country;
    private String IDType;
    private String IDNumber;
    private String  fullName;
    private String DOB;
    private String photo;
    private String phoneNumber;
    private String gender;
    private String address;
}
