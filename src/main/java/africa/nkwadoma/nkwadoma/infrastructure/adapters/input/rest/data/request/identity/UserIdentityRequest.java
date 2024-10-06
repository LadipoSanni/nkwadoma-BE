package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityRequest {
    private String email;
    private String password;

}
