package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.MFAType;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.INVALID_ROLE;

@Slf4j
@Setter
@Getter
@Builder
@ToString
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
    private LocalDateTime createdAt;
    private String image;
    private String gender;
    private String dateOfBirth;
    private String stateOfOrigin;
    private String maritalStatus;
    private String stateOfResidence;
    private String nationality;
    private String residentialAddress;
    //
    private String lgaOfOrigin;
    private String middleName;
    private String State;
    private String lgaOfResidence;
    private String nameOnCard;
    //
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
    private String nin;
    private String address;
    private String taxId;
    private boolean additionalDetailsCompleted;
    private NextOfKin nextOfKin;
    private BankDetail bankDetail;

    private String  MFAPhoneNumber;
    private MFAType mfaType;

    public void validate() throws MeedlException {
        log.info("Started validating for user identity in validation");
        if (ObjectUtils.isEmpty(this.role)|| StringUtils.isEmpty(this.role.name()))
            throw new IdentityException(INVALID_ROLE.getMessage());
        MeedlValidator.validateEmail(this.email);
        MeedlValidator.validateDataElement(this.firstName, UserMessages.INVALID_FIRST_NAME.getMessage());
        MeedlValidator.validateDataElement(this.lastName, UserMessages.INVALID_LAST_NAME.getMessage());
        MeedlValidator.validateUUID(this.createdBy, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        log.info("Creator ID: {}", this.createdBy);
        log.info("Finished validating for user identity in validation");
    }

}
