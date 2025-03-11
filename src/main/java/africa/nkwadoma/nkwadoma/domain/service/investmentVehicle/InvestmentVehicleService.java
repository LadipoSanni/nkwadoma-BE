package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleMapper;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;


import java.time.LocalDate;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_EXIST;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.InvestmentVehicleConstants.INVESTMENT_VEHICLE_URL;

@RequiredArgsConstructor

public class InvestmentVehicleService implements InvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final InvestmentVehicleMapper investmentVehicleMapper;

    @Override
    public InvestmentVehicle setUpInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment Vehicle Object Cannot Be Null");
        if (ObjectUtils.isNotEmpty(investmentVehicle.getInvestmentVehicleStatus()) &&
                investmentVehicle.getInvestmentVehicleStatus().equals(DRAFT)){
            investmentVehicle.validateDraft();
            investmentVehicle.setLastUpdatedDate(LocalDate.now());
            return saveInvestmentVehicleToDraft(investmentVehicle);
        }
        return createInvestmentVehicle(investmentVehicle);
    }

    private InvestmentVehicle saveInvestmentVehicleToDraft(InvestmentVehicle investmentVehicle) throws MeedlException {
        if (ObjectUtils.isNotEmpty(investmentVehicle.getId())){
            return updateInvestmentVehicleInDraft(investmentVehicle);
        }
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    private InvestmentVehicle updateInvestmentVehicleInDraft(InvestmentVehicle investmentVehicle) throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                investmentVehicleOutputPort.findById(investmentVehicle.getId());
        investmentVehicleMapper.updateInvestmentVehicle(foundInvestmentVehicle,
                investmentVehicle);
        return investmentVehicleOutputPort.save(foundInvestmentVehicle);
    }

    private InvestmentVehicle createInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        investmentVehicle.validate();
        checkIfInvestmentVehicleNameExist(investmentVehicle);
        investmentVehicle.setValues();
        return investmentVehicleOutputPort.save(investmentVehicle);
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

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateDataElement(String.valueOf(type), InvestmentVehicleMessages.INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL.getMessage());
        return investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize, pageNumber, type);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByStatus(int pageSize, int pageNumber, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateObjectInstance(status, InvestmentVehicleMessages.INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());
        return investmentVehicleOutputPort.findAllInvestmentVehicleByStatus(pageSize, pageNumber, status);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByTypeAndStatus(int pageSize, int pageNumber, InvestmentVehicleType type, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validateObjectInstance(type, InvestmentVehicleMessages.INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateObjectInstance(status, InvestmentVehicleMessages.INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());
        return investmentVehicleOutputPort.findAllInvestmentVehicleByTypeAndStatus(pageSize, pageNumber, type, status);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleBy(Integer pageSize, Integer pageNumber, InvestmentVehicleType investmentVehicleType, InvestmentVehicleStatus investmentVehicleStatus, FundRaisingStatus fundRaisingStatus) throws MeedlException {
            Integer size = MeedlValidator.validateAndSetPaginationForPageSize(pageSize);
            Integer number = MeedlValidator.validateAndSetPaginationForPageNumber(pageNumber);
        return investmentVehicleOutputPort.findAllInvestmentVehicleBy(size, number, investmentVehicleType, investmentVehicleStatus, fundRaisingStatus);
    }

    private String generateInvestmentVehicleLink(String id) {
        return INVESTMENT_VEHICLE_URL+id;
    }


}
