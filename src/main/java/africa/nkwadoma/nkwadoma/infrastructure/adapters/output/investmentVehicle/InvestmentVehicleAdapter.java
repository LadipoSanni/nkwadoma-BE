package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestmentVehicleEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;


@Slf4j
@RequiredArgsConstructor
public class InvestmentVehicleAdapter implements InvestmentVehicleOutputPort {

    private final InvestmentVehicleEntityRepository investmentVehicleRepository;
    private final InvestmentVehicleMapper investmentVehicleMapper;

    @Override
    public InvestmentVehicle save(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle, INVESTMENT_VEHICLE_CANNOT_BE_NULL.getMessage());
        if (ObjectUtils.isNotEmpty(investmentVehicle.getInvestmentVehicleStatus()) &&
                investmentVehicle.getInvestmentVehicleStatus().equals(DRAFT)){
            investmentVehicle.validateDraft();
            return saveAndGetInvestmentVehicle(investmentVehicle);
        }
        investmentVehicle.validate();
        investmentVehicle.setTotalAvailableAmount(investmentVehicle.getSize());
        log.info("saving vehicle size as available amount {}", investmentVehicle.getTotalAvailableAmount());
        return saveAndGetInvestmentVehicle(investmentVehicle);
    }

    private InvestmentVehicle saveAndGetInvestmentVehicle(InvestmentVehicle investmentVehicle) {
        InvestmentVehicleEntity investmentEntity =
                investmentVehicleMapper.toInvestmentVehicleEntity(investmentVehicle);
        investmentEntity = investmentVehicleRepository.save(investmentEntity);
        return investmentVehicleMapper.toInvestmentVehicle(investmentEntity);
    }


    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicle(int pageSize, int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findAll(pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateObjectInstance(type, INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("startDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findByInvestmentVehicleType(type, pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public List<InvestmentVehicle> findAllInvestmentVehicleByStatus(InvestmentVehicleStatus investmentVehicleStatus) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicleStatus, INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());
        List<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findByInvestmentVehicleStatus(investmentVehicleStatus);
        return investmentVehicleEntities.stream().map(investmentVehicleMapper::toInvestmentVehicle).collect(Collectors.toList());
    }

    @Override
    public InvestmentVehicle findByNameExcludingDraftStatus(String name, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validateObjectName(name, INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY.getMessage());
        InvestmentVehicleEntity investmentVehicleEntity =
                investmentVehicleRepository.findByNameAndStatusNotDraft(name,status);
        return investmentVehicleMapper.toInvestmentVehicle(investmentVehicleEntity);
    }

    @Override
    public List<InvestmentVehicle> searchInvestmentVehicle(String name) throws MeedlException {
        MeedlValidator.validateObjectName(name, INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY.getMessage());
        List<InvestmentVehicleEntity> investmentVehicles =
                investmentVehicleRepository.findAllByNameContainingIgnoreCase(name);
        return investmentVehicles.stream().map(investmentVehicleMapper::toInvestmentVehicle).collect(Collectors.toList());
    }


    @Override
    public InvestmentVehicle findById(String id) throws MeedlException {
        if (id != null){
            InvestmentVehicleEntity investmentVehicleEntity =
                    investmentVehicleRepository.findById(id).orElseThrow(()->new InvestmentException(INVESTMENT_VEHICLE_NOT_FOUND.getMessage()));
            return investmentVehicleMapper.toInvestmentVehicle(investmentVehicleEntity);
        }
        throw new InvestmentException(INVESTMENT_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

    @Override
    public void deleteInvestmentVehicle(String investmentVehicleId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY.getMessage());
        investmentVehicleRepository.deleteById(investmentVehicleId);
    }


}
