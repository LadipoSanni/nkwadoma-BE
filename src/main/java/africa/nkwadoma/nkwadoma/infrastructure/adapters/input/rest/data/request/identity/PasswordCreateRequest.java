package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordCreateRequest {
    @NotBlank(message= "Token is required")
    private String token;
    @NotBlank(message= "Password is required")
    private String password;
}
