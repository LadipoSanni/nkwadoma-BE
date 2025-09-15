package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.identity.MFAType;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LevelOfEducation;
import lombok.*;

@Setter
@Getter
public class UserIdentityResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean isIdentityVerified;
    private boolean enabled;
    private String createdAt;
    private String image;
    private String gender;
    private String dateOfBirth;
    private String stateOfOrigin;
    private String maritalStatus;
    private String stateOfResidence;
    private String levelOfEduction;
    private String nationality;
    private String residentialAddress;
    private IdentityRole role;
    private String createdBy;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String accessToken;
    private String refreshToken;
    private String organizationDomain;
    private String deactivationReason;
    private String reactivationReason;
    private boolean additionalDetailsCompleted;
    private NextOfKinResponse nextOfKinResponse;

    private String  MFAPhoneNumber;
    private MFAType mfaType;
}
