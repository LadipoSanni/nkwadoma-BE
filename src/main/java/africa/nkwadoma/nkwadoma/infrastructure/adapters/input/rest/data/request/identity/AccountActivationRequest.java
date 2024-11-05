package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountActivationRequest {
    @NotBlank(message = "The id is required")
    private String id;
    @NotBlank(message = "Please provide a valid reason")
    private String reason;
}
