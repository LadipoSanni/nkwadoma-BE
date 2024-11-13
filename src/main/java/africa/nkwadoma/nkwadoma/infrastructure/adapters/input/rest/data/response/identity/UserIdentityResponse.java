package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserIdentityResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean enabled;
    private String createdAt;
    private IdentityRole role;
    private String createdBy;
    private String password;
    private String accessToken;
    private String refreshToken;
    private String newPassword;
    private String organizationDomain;
    private String deactivationReason;
    private String reactivationReason;
}
