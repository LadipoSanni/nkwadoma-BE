package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.INVALID_VALID_ROLE;

@Slf4j
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean isIdentityVerified;
    private boolean enabled;
    private String createdAt;
    private String image;
    private IdentityRole role;
    private String createdBy;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String password;
    private String accessToken;
    private String refreshToken;
    private String newPassword;
    private String organizationDomain;
    private String deactivationReason;
    private String reactivationReason;
    private String bvn;

    public void validate() throws MeedlException {
        log.info("Started validating for user identity in validation");
        if (ObjectUtils.isEmpty(this.role)|| StringUtils.isEmpty(this.role.name()))
            throw new IdentityException(INVALID_VALID_ROLE.getMessage());
        MeedlValidator.validateEmail(this.email);
        MeedlValidator.validateDataElement(this.firstName);
        MeedlValidator.validateDataElement(this.lastName);
        MeedlValidator.validateUUID(this.createdBy);
    }

}
