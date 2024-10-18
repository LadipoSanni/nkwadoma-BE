package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityRequest {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean enabled;
    private String role;
    private String password;
    private String newPassword;
    private String organizationDomain;
    private String reasonForDeactivation;
    private String reasonForReactivation;
}
