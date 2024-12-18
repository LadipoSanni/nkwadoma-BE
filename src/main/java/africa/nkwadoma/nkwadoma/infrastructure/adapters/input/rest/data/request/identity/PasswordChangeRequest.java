package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequest {
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "New password is required")
    private String newPassword;
}
