package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityVerificationRequest {
//    @Pattern(regexp = "^\\d{11}$", message = "BVN must contain exactly 11 digits and no alphabets.")
    private String bvn;
//    @Pattern(regexp = "^\\d{11}$", message = "Nin must contain exactly 11 digits and no alphabets.")
    private String nin;
//    @NotBlank(message = "Token not present")
    private String token;
}
