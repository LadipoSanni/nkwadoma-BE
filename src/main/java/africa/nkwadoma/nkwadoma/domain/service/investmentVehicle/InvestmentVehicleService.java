package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.MeedlPortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;


import java.math.BigInteger;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_EXIST;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.InvestmentVehicleConstants.INVESTMENT_VEHICLE_URL;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType.COMMERCIAL;

@RequiredArgsConstructor

public class InvestmentVehicleService implements CreateInvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final MeedlPortfolioOutputPort meedlPortfolioOutputPort;

    @Override
    public InvestmentVehicle createInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment Vehicle Object Cannot Be Null");
        if (ObjectUtils.isNotEmpty(investmentVehicle.getInvestmentVehicleStatus()) &&
                investmentVehicle.getInvestmentVehicleStatus().equals(DRAFT)){
             return investmentVehicleOutputPort.save(investmentVehicle);
        }
        investmentVehicle.validate();
        checkIfInvestmentVehicleNameExist(investmentVehicle);
        setInvestmentVehicleNumbersOnMuddlePortfolio(investmentVehicle);
        investmentVehicle.setValues();
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    private void setInvestmentVehicleNumbersOnMuddlePortfolio(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlPortfolio meedlPortfolio = meedlPortfolioOutputPort.findMeedlPortfolio();
        if (investmentVehicle.getInvestmentVehicleType().equals(COMMERCIAL)){
            meedlPortfolio.setTotalNumberOfCommercialFundsInvestmentVehicles(
                    meedlPortfolio.getTotalNumberOfCommercialFundsInvestmentVehicles() + BigInteger.ONE.intValue()
            );
        }else {
            meedlPortfolio.setTotalNumberOfEndowmentFundsInvestmentVehicles(
                    meedlPortfolio.getTotalNumberOfEndowmentFundsInvestmentVehicles() + BigInteger.ONE.intValue()
            );
        }
        meedlPortfolio.setTotalNumberOfInvestmentVehicles(
                meedlPortfolio.getTotalNumberOfInvestmentVehicles() + BigInteger.ONE.intValue()
        );
        meedlPortfolioOutputPort.save(meedlPortfolio);
    }

    private void checkIfInvestmentVehicleNameExist(InvestmentVehicle investmentVehicle) throws MeedlException {
        InvestmentVehicle existingVehicle = investmentVehicleOutputPort.findByNameExcludingDraftStatus(investmentVehicle.getName(),DRAFT);
        if (!ObjectUtils.isEmpty(existingVehicle)) {
            throw new InvestmentException(INVESTMENT_VEHICLE_NAME_EXIST.getMessage());
        }
    }

    @Override
    public void deleteInvestmentVehicle(String investmentId) throws MeedlException {
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentId);
    }

    @Override
    public InvestmentVehicle viewInvestmentVehicleDetails(String id) throws MeedlException {
        return investmentVehicleOutputPort.findById(id);
    }


    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicle(int pageSize, int pageNumber) {
        return investmentVehicleOutputPort.findAllInvestmentVehicle(pageSize, pageNumber);
    }

    @Override
    public List<InvestmentVehicle> searchInvestmentVehicle(String investmentVehicleName) throws MeedlException {
        MeedlValidator.validateDataElement(investmentVehicleName,
                InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY.getMessage());
        return investmentVehicleOutputPort.searchInvestmentVehicle(investmentVehicleName);
    }

    @Override
    public InvestmentVehicle publishInvestmentVehicle(String investmentVehicleId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId,"Invalid investment vehicle Id");
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
        if (ObjectUtils.isNotEmpty(investmentVehicle.getInvestmentVehicleStatus())&&
        investmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.DRAFT)){
            investmentVehicle.validate();
        }
        investmentVehicle.setInvestmentVehicleStatus(InvestmentVehicleStatus.PUBLISHED);
        String investmentVehicleLink = generateInvestmentVehicleLink(investmentVehicle.getId());
        investmentVehicle.setInvestmentVehicleLink(investmentVehicleLink);
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    private String generateInvestmentVehicleLink(String id) {
        return INVESTMENT_VEHICLE_URL+id;
    }


}
