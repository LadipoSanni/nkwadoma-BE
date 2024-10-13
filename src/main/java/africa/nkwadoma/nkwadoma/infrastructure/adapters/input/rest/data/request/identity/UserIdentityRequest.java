package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityRequest {
//    @NotBlank(message = "Email address must not be empty")
    private String email;
//    @NotBlank(message = "Password must not be empty")
    private String password;

}
