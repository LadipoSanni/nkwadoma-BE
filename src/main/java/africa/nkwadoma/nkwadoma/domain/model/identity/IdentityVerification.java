package africa.nkwadoma.nkwadoma.domain.model.identity;


import africa.nkwadoma.nkwadoma.domain.enums.IdentityVerificationEnum;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvalidInputException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentityVerification {

    private String identityId;
    private String bvn;
    private String nin;
    private String loanReferralId;
    private String imageUrl;
    private String email;
    private String token;
    private IdentityVerificationEnum typeOfIdentity;

    public void validate() throws MeedlException {
        log.info("Validation started. Bvn and nin check");

        String BVN_NIN_REGEX = "^\\d{11}$";
        Pattern pattern = Pattern.compile(BVN_NIN_REGEX);
        MeedlValidator.validateDataElement(this.bvn, "Bvn is required");
    MeedlValidator.validateDataElement(this.nin, "Nin is required");

        if (!pattern.matcher(this.bvn).matches()) {
            throw new InvalidInputException("Please provide a valid bvn");
        }
        if (!pattern.matcher(this.nin).matches()) {
            throw new InvalidInputException("Please provide a valid nin");
        }
    }
    public void validateImageUrl() throws MeedlException {
        log.info("Validation image is present.");
        MeedlValidator.validateDataElement(this.imageUrl, "Image is required");
    }
}

