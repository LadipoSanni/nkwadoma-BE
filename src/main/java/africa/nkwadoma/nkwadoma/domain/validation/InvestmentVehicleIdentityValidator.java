package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import org.apache.commons.lang3.ObjectUtils;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentMessages.INVESTMENT_IDENTITY_CANNOT_BE_NULL;

public class InvestmentVehicleIdentityValidator extends MiddleValidator {

    public static void validateInvestmentIdentityValidator(InvestmentVehicleIdentity investmentVehicleIdentity) throws  MiddlException {
        if (ObjectUtils.isEmpty(investmentVehicleIdentity)){
            throw new InvestmentException(INVESTMENT_IDENTITY_CANNOT_BE_NULL.getMessage());
        }
        validateDataElement(investmentVehicleIdentity.getName());
        validateDataElement(investmentVehicleIdentity.getTenure());
        validateDataElement(String.valueOf(investmentVehicleIdentity.getRate()));
        validateDataElement(investmentVehicleIdentity.getSponsors());
        validateDataElement(investmentVehicleIdentity.getInvestmentVehicleType().toString());
        validateDataElement(investmentVehicleIdentity.getMandate());
    }


}
