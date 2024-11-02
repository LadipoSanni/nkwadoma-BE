package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.INVALID_EMAIL_ADDRESS;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.INVALID_INDUSTRY_OR_SERVICE_OFFERING;

@Slf4j
public class OrganizationIdentityValidator extends MeedlValidator {

    public static void validateOrganizationIdentity(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("The organization being validated : {}", organizationIdentity);
        if (ObjectUtils.isEmpty(organizationIdentity)){
            throw new IdentityException(ORGANIZATION_IDENTITY_CANNOT_BE_NULL.getMessage());
        }
        log.info("{}",organizationIdentity.getServiceOfferings());
        if (organizationIdentity.getServiceOfferings() == null
            || organizationIdentity.getServiceOfferings().get(0).getIndustry() == null) {
            throw new IdentityException(INVALID_INDUSTRY_OR_SERVICE_OFFERING.getMessage());
        }
        validateEmail(organizationIdentity.getEmail());
        validateDataElement(organizationIdentity.getName());
        validateDataElement(organizationIdentity.getServiceOfferings().get(0).getIndustry().name());
        validateDataElement(organizationIdentity.getRcNumber());
        validateDataElement(organizationIdentity.getPhoneNumber());
        log.info("Organization identity validation completed successfully {}", organizationIdentity);

    }

}
