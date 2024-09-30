package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.regex.Pattern;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MiddlMessages.*;

public class UserIdentityValidator extends MiddleValidator {
     public static void validateUserIdentity(List<OrganizationEmployeeIdentity> userIdentities) throws MiddlException {
         if (CollectionUtils.isEmpty(userIdentities)){
             throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
         }
         for(OrganizationEmployeeIdentity userIdentity : userIdentities){
             validateUserIdentity(userIdentity.getMiddlUser());
         }
     }

     public static void validateUserIdentity(UserIdentity userIdentity) throws MiddlException {
         if (ObjectUtils.isEmpty(userIdentity)){
             throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
         }
         validateEmail(userIdentity.getEmail());
         validateUserDataElement(userIdentity.getFirstName());
         validateUserDataElement(userIdentity.getLastName());
         validateUserDataElement(userIdentity.getCreatedBy());
         validateUserDataElement(userIdentity.getRole());
     }

    private static void validateEmail(UserIdentity userIdentity) throws IdentityException {
        if (StringUtils.isEmpty(userIdentity.getEmail()) || !EmailValidator.getInstance().isValid(userIdentity.getEmail().trim())) {
            throw new IdentityException(INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateEmailDomain(String inviteeEmail, String inviterEmail) throws IdentityException {
         if (!compareEmailDomain(inviteeEmail,inviterEmail)){
             throw new IdentityException(DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage());
         }

    }

    private static boolean compareEmailDomain(String inviteeEmail, String inviterEmail) {
        String inviteeEmailDomain =
                inviteeEmail.substring(inviteeEmail.indexOf(EMAIL_INDEX.getMessage()));
        String inviterEmailDomain = inviterEmail.substring(inviterEmail.indexOf(EMAIL_INDEX.getMessage()));
        return StringUtils.equals(inviterEmailDomain, inviteeEmailDomain);
    }

    public static void validatePassword(String password) throws MiddlException {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN.getMessage());
        if (!pattern.matcher(password).matches()){
            throw new IdentityException(INVALID_PASSWORD.getMessage());
        }
    }



}
