package africa.nkwadoma.nkwadoma.domain.model.identity;


import africa.nkwadoma.nkwadoma.domain.exceptions.InvalidInputException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String identityImage;
    private IdentityVerificationEnum typeOfIdentity;

    public void validate() throws InvalidInputException {
        log.info("Validation started");
        boolean bvnIsEmpty = Boolean.FALSE;
        boolean ninIsEmpty = Boolean.FALSE;
        boolean isError = Boolean.FALSE;
        String message = "";
        String BVN_NIN_REGEX = "^\\d{11}$";
        Pattern pattern = Pattern.compile(BVN_NIN_REGEX);

        if (StringUtils.isNotEmpty(this.bvn)){
            if (!pattern.matcher(this.bvn).matches()){
                message = "Please provide a valid bvn";
                isError = Boolean.TRUE;
            }
        }else {
            bvnIsEmpty = Boolean.TRUE;
        }

        if (StringUtils.isNotEmpty(this.nin)){
            if (!pattern.matcher(this.nin).matches()){
                throw new InvalidInputException("Please provide a valid nin");
            }
        }else {
            ninIsEmpty = Boolean.TRUE;
        }

    }

    private enum IdentityVerificationEnum{
        BVN, NIN
    }
}

