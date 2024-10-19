package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email must not be empty")
    private String email;
    @NotBlank(message = "Password must not be empty")
    private String password;

    public void setEmail(@NotBlank(message = "Email must not be empty") String email) {
        this.email = email.trim();
    }

    public void setPassword(@NotBlank(message = "Password must not be empty") String password) {
        this.password = password.trim();
    }
}
