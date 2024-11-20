package africa.nkwadoma.nkwadoma.domain.model.identity;


import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;

import java.util.regex.*;

@Slf4j
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentityVerification {

    private String identityId;
    private String bvn;
    private String nin;
    private String email;
    private String identityImage;
    private String token;
    private IdentityVerificationEnum typeOfIdentity;

    private String country;
    private String IDType;
    private String IDNumber;
    private String  fullName;
    private String DOB;
    private String photo;
    private String phoneNumber;
    private String gender;
    private String address;



    public void validate() throws MeedlException {
        log.info("Validation starting bvn and nin");

        String BVN_NIN_REGEX = "^\\d{11}$";
        Pattern pattern = Pattern.compile(BVN_NIN_REGEX);
        MeedlValidator.validateDataElement(this.bvn);
        MeedlValidator.validateDataElement(this.nin);

        if (!pattern.matcher(this.bvn).matches()) {
            throw new InvalidInputException("Please provide a valid bvn");
        }
        if (!pattern.matcher(this.nin).matches()) {
            throw new InvalidInputException("Please provide a valid nin");
        }

    }

    private enum IdentityVerificationEnum {
        BVN, NIN
    }
}

