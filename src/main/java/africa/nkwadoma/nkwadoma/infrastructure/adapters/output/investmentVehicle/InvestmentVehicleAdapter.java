package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;


@Slf4j
@RequiredArgsConstructor
public class InvestmentVehicleAdapter implements InvestmentVehicleOutputPort {

    private final InvestmentVehicleEntityRepository investmentVehicleRepository;
    private final InvestmentVehicleMapper investmentVehicleMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;

    @Override
    public InvestmentVehicle save(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle, INVESTMENT_VEHICLE_CANNOT_BE_NULL.getMessage());
        log.info("Investment vehicle at the adapter level before being seved {}", investmentVehicle);
            investmentVehicle.validateDraft();
            log.info("saving vehicle size as available amount for draft {}", investmentVehicle.getTotalAvailableAmount());
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
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findByInvestmentVehicleType(type, pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleByStatus(int pageSize, int pageNumber, InvestmentVehicleStatus investmentVehicleStatus) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateObjectInstance(investmentVehicleStatus, INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,Sort.by("lastUpdatedDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findByInvestmentVehicleStatus(investmentVehicleStatus, pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleByTypeAndStatus(int pageSize, int pageNumber, InvestmentVehicleType type, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validateObjectInstance(type, InvestmentVehicleMessages.INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateObjectInstance(status, InvestmentVehicleMessages.INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findByInvestmentVehicleTypeAndStatus(type, status, pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleBy(int pageSize, int pageNumber, InvestmentVehicle investmentVehicle, String sortField, String userId) throws MeedlException {
        Sort sort = getSortValue(sortField, investmentVehicle.getInvestmentVehicleStatus());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<InvestmentVehicleEntity> investmentVehicleEntities;

        InvestmentVehicleType investmentVehicleType = investmentVehicle.getInvestmentVehicleType();
        InvestmentVehicleStatus investmentVehicleStatus = investmentVehicle.getInvestmentVehicleStatus();
        InvestmentVehicleMode investmentVehicleMode = investmentVehicle.getVehicleOperation().getFundRaisingStatus();

        if (isFinancier(userId)) {
            investmentVehicleEntities = investmentVehicleRepository
                    .findAllInvestmentVehicleForFinancier(investmentVehicleType, investmentVehicleStatus, investmentVehicleMode, userId, pageable);
        } else {
            investmentVehicleEntities = investmentVehicleRepository
                    .findAllInvestmentVehicleBy(investmentVehicleType, investmentVehicleStatus, investmentVehicleMode, pageable);
            log.info("investment vehicle pm {}",investmentVehicleEntities);
        }
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    private boolean isFinancier(String userId) throws MeedlException {
        return userIdentityOutputPort.findById(userId).getRole().equals(IdentityRole.FINANCIER);
    }

    private Sort getSortValue(String sortField, InvestmentVehicleStatus investmentVehicleStatus) {
        Sort sort = (sortField == null || sortField.isEmpty())
                ? Sort.by("createdDate").descending()
                : Sort.by(sortField).descending();
        if (investmentVehicleStatus != null && investmentVehicleStatus.equals(DRAFT)) {
            sort = (sortField == null || sortField.isEmpty())
                    ? Sort.by("lastUpdatedDate").descending()
                    : Sort.by(sortField).descending();
        }
        return sort;
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleByFundRaisingStatus(int pageSize, int pageNumber, FundRaisingStatus fundRaisingStatus) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validateObjectInstance(fundRaisingStatus, "Investment vehicle fundRaising Status cannot be empty or null");

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findByInvestmentVehicleByFundRaisingStatus(fundRaisingStatus, pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleExcludingPrivate(String userId,int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities =
                investmentVehicleRepository.findAllInvestmentVehicleExcludingPrivate(userId,pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> searchInvestmentVehicleExcludingPrivate(String userId, InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);

        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities =
                investmentVehicleRepository.findAllByNameContainingIgnoreCaseAndInvestmentVehicleTypeAndStatusExcludingPrivateAndDefault
                        (userId,investmentVehicle.getInvestmentVehicleStatus(),
                                investmentVehicle.getInvestmentVehicleType(),investmentVehicle.getName(),pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicleFinancierWasAddedTo(String userId, InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);


        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicleEntities =
                investmentVehicleRepository.findAllInvestmentVehicleFinancierWasAddedToByInvestmentVehicleType(userId,
                                investmentVehicle.getInvestmentVehicleType(),pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }


    @Override
    public InvestmentVehicle findByNameExcludingDraftStatus(String name, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validateObjectName(name, INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY.getMessage(),"Investment vehicle");
        InvestmentVehicleEntity investmentVehicleEntity =
                investmentVehicleRepository.findByNameAndStatusNotDraft(name,status);
        return investmentVehicleMapper.toInvestmentVehicle(investmentVehicleEntity);
    }

    @Override
    public Page<InvestmentVehicle> searchInvestmentVehicle(String name, InvestmentVehicle investmentVehicle,int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle.getInvestmentVehicleStatus(), "Investment vehicle status cannot be empty");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<InvestmentVehicleEntity> investmentVehicles =
                investmentVehicleRepository.findAllByNameContainingIgnoreCaseAndInvestmentVehicleTypeAndStaus(name,
                        investmentVehicle.getInvestmentVehicleType(),investmentVehicle.getInvestmentVehicleStatus(),pageRequest);
        return investmentVehicles.map(investmentVehicleMapper::toInvestmentVehicle);
    }


    @Override
    public InvestmentVehicle findById(String id) throws MeedlException {
        if (id != null){
            InvestmentVehicleEntity investmentVehicleEntity =
                    investmentVehicleRepository.findById(id).orElseThrow(()->new InvestmentException(INVESTMENT_VEHICLE_NOT_FOUND.getMessage()));
            return investmentVehicleMapper.toInvestmentVehicle(investmentVehicleEntity);
        }
        throw new InvestmentException(InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
    }

    @Override
    public void deleteInvestmentVehicle(String investmentVehicleId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY.getMessage());
        investmentVehicleRepository.deleteById(investmentVehicleId);
    }


}
