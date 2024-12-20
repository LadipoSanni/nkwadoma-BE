package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentMessages.INVESTMENT_VEHICLE_NAME_EXIST;

@RequiredArgsConstructor

public class InvestmentVehicleService implements CreateInvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;

    @Override
    public InvestmentVehicle createInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment Vehicle Object Cannot Be Null");
        investmentVehicle.validate();
        checkIfInvestmentVehicleNameExist(investmentVehicle);
        investmentVehicle.setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);
        investmentVehicle.setStartDate(LocalDate.now());
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    private void checkIfInvestmentVehicleNameExist(InvestmentVehicle investmentVehicle) throws MeedlException {
        InvestmentVehicle existingVehicle = investmentVehicleOutputPort.findByName(investmentVehicle.getName());
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


}
