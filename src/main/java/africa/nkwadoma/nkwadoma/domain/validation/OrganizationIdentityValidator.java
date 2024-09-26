package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.ORGANIZATION_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.INVALID_EMAIL_ADDRESS;

public class OrganizationIdentityValidator extends MiddleValidator {

    public static void validateOrganizationIdentity(OrganizationIdentity organizationIdentity) throws IdentityException {
        if (ObjectUtils.isEmpty(organizationIdentity)){
            throw new IdentityException(ORGANIZATION_IDENTITY_CANNOT_BE_NULL);
        }

        try{
            validateEmail(organizationIdentity.getEmail());
            validateUserDataElement(organizationIdentity.getName());
            validateUserDataElement(organizationIdentity.getIndustry());
            validateUserDataElement(organizationIdentity.getTin());
            validateUserDataElement(organizationIdentity.getRcNumber());
            validateUserDataElement(organizationIdentity.getPhoneNumber());
            validateUserDataElement(organizationIdentity.getWebsiteAddress());
        }catch (MiddlException exception){
            throw new IdentityException(exception.getMessage());
        }
    }

    private static void validateEmail(OrganizationIdentity organizationIdentity) throws IdentityException {
        if (StringUtils.isEmpty(organizationIdentity.getEmail()) || !EmailValidator.getInstance().isValid(organizationIdentity.getEmail().trim())) {
            throw new IdentityException(INVALID_EMAIL_ADDRESS);
        }
    }

}
