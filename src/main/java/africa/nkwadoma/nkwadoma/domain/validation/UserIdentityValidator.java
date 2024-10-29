package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.regex.Pattern;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;

@Slf4j
public class UserIdentityValidator extends MeedlValidator {
     public static void validateUserIdentity(List<OrganizationEmployeeIdentity> userIdentities) throws MeedlException {
         log.info("Started validdating for user identities (List) : {}", userIdentities);
         log.info("validating to check for empty list : {}", CollectionUtils.isEmpty(userIdentities));
         if (CollectionUtils.isEmpty(userIdentities)){
             log.error("{} - {}", USER_IDENTITY_CANNOT_BE_NULL.getMessage(), userIdentities);
             throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
         }
         for(OrganizationEmployeeIdentity userIdentity : userIdentities){
             validateUserIdentity(userIdentity.getMiddlUser());
         }
         log.info("Users identity validation completed... for user {} ", userIdentities);
     }

     public static void validateUserIdentity(UserIdentity userIdentity) throws MeedlException {
         log.info("Started validating for user identity in validation class : {}", userIdentity);
         MeedlValidator.validateObjectInstance(userIdentity);
         if (ObjectUtils.isEmpty(userIdentity.getRole())|| StringUtils.isEmpty(userIdentity.getRole().name()))
             throw new IdentityException(INVALID_VALID_ROLE.getMessage());

         MeedlValidator.validateEmail(userIdentity.getEmail());
         MeedlValidator.validateDataElement(userIdentity.getFirstName());
         MeedlValidator.validateDataElement(userIdentity.getLastName());
         MeedlValidator.validateUUID(userIdentity.getCreatedBy());
     }

    private static void validateUserEmail(String email) throws IdentityException {
            if (StringUtils.isEmpty(email) || !EmailValidator.getInstance().isValid(email)) {
                throw new IdentityException(INVALID_EMAIL_ADDRESS.getMessage());
            }
        }

    public static void validateEmailDomain(String inviteeEmail, String inviterEmail) throws IdentityException {
         validateUserEmail(inviteeEmail);
         validateUserEmail(inviterEmail);
         if (!compareEmailDomain(inviteeEmail,inviterEmail)){
             log.error("{} - {} : {}",DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage(), inviteeEmail, inviterEmail);
             throw new IdentityException(DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage());
         }

    }

    private static boolean compareEmailDomain(String inviteeEmail, String inviterEmail) {
        String inviteeEmailDomain =
                inviteeEmail.substring(inviteeEmail.indexOf(EMAIL_INDEX.getMessage()));
        String inviterEmailDomain = inviterEmail.substring(inviterEmail.indexOf(EMAIL_INDEX.getMessage()));
        return StringUtils.equals(inviterEmailDomain, inviteeEmailDomain);
    }

    public static void validatePassword(String password) throws MeedlException {
        validateDataElement(password);
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN.getMessage());
        if (!pattern.matcher(password).matches()){
            throw new IdentityException(WEAK_PASSWORD.getMessage());
        }
    }






}
