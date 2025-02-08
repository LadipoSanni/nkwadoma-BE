package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityRequest {

    private String id;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "First Name is required")
    private String firstName;
    @NotBlank(message = "Last Name is required")
    private String lastName;
    private String phoneNumber;
    private String createdBy;
    private boolean emailVerified;
    private boolean enabled;
    private String role;
    private String password;
    private String newPassword;
    private String organizationDomain;
    private String reactivationReason;
    private String deactivationReason;
}
