package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import org.apache.commons.lang3.ObjectUtils;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentMessages.INVESTMENT_IDENTITY_CANNOT_BE_NULL;

public class InvestmentVehicleValidator extends MeedlValidator {

    public static void validateInvestmentVehicle(InvestmentVehicle investmentVehicle) throws  MeedlException {
        if (ObjectUtils.isEmpty(investmentVehicle)){
            throw new InvestmentException(INVESTMENT_IDENTITY_CANNOT_BE_NULL.getMessage());
        }
        validateDataElement(investmentVehicle.getName());
        validateDataElement(investmentVehicle.getTenure());
        validateFloatDataElement(investmentVehicle.getRate());
        validateDataElement(investmentVehicle.getSponsors());
        validateDataElement(investmentVehicle.getInvestmentVehicleType().toString());
        validateDataElement(investmentVehicle.getMandate());
        validateBigDecimalDataElement(investmentVehicle.getSize());
    }




}
