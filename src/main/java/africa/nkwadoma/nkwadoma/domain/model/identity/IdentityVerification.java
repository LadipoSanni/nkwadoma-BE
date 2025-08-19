package africa.nkwadoma.nkwadoma.domain.model.identity;


import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityVerificationEnum;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.commons.IdentityVerificationMessage;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.*;

@Slf4j
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class IdentityVerification {

    private String identityId;
    private String encryptedBvn;
    private String encryptedNin;
    private String decryptedBvn;
    private String decryptedNin;
    private String loanReferralId;
    private String imageUrl;
    private String email;
    private String token;
    private String test;
    private IdentityVerificationEnum typeOfIdentity;

    public void validate() throws MeedlException {
        log.info("Validation started. Bvn {} and nin {} check", this.decryptedBvn, decryptedNin);

        String BVN_NIN_REGEX = "^\\d{11}$";
        Pattern pattern = Pattern.compile(BVN_NIN_REGEX);
        MeedlValidator.validateDataElement( this.decryptedBvn, IdentityVerificationMessage.INVALID_BVN.getMessage());
        MeedlValidator.validateDataElement(this.decryptedNin, IdentityVerificationMessage.INVALID_NIN.getMessage());

        if (!pattern.matcher(this.decryptedBvn).matches()) {
            throw new IdentityException(IdentityVerificationMessage.PROVIDE_VALID_BVN.getMessage());
        }
        if (!pattern.matcher(this.decryptedNin).matches()) {
            throw new IdentityException(IdentityVerificationMessage.PROVIDE_VALID_NIN.getMessage());
        }
    }
    public void validateImageUrl() throws MeedlException {
        log.info("Validation image is present.");
        MeedlValidator.validateDataElement(this.imageUrl, "Image is required");
    }
}

