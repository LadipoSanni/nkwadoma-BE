package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MiddlMessages.INVALID_EMAIL_ADDRESS;

public class OrganizationIdentityValidator extends MiddleValidator {

    public static void  validateOrganizationIdentity(OrganizationIdentity organizationIdentity) throws MiddlException {
        if (ObjectUtils.isEmpty(organizationIdentity)){
            throw new IdentityException(ORGANIZATION_IDENTITY_CANNOT_BE_NULL.getMessage());
        }
        validateEmail(organizationIdentity.getEmail());
        validateDataElement(organizationIdentity.getName());
        validateDataElement(organizationIdentity.getIndustry());
        validateDataElement(organizationIdentity.getRcNumber());
        validateDataElement(organizationIdentity.getPhoneNumber());

    }

    private static void validateEmail(OrganizationIdentity organizationIdentity) throws IdentityException {
        if (StringUtils.isEmpty(organizationIdentity.getEmail()) || !EmailValidator.getInstance().isValid(organizationIdentity.getEmail().trim())) {
            throw new IdentityException(INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

}
