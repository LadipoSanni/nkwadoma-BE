package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.*;

public class UserIdentityValidator extends MiddleValidator {
     public static void validateUserIdentity(UserIdentity userIdentity) throws IdentityException {
         if (ObjectUtils.isEmpty(userIdentity)){
             throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL);
         }

         try{
             validateEmail(userIdentity.getEmail());
             validateUserDataElement(userIdentity.getFirstName());
             validateUserDataElement(userIdentity.getLastName());
             validateUserDataElement(userIdentity.getCreatedBy());
             validateUserDataElement(userIdentity.getRole());
         }catch (MiddlException exception){
             throw new IdentityException(exception.getMessage());
         }
     }

    private static void validateEmail(UserIdentity userIdentity) throws IdentityException {
        if (StringUtils.isEmpty(userIdentity.getEmail()) || !EmailValidator.getInstance().isValid(userIdentity.getEmail().trim())) {
            throw new IdentityException(INVALID_EMAIL_ADDRESS);
        }
    }

}
