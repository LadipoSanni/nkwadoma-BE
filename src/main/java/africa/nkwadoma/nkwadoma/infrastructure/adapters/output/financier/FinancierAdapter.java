package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinancierAdapter implements FinancierOutputPort {
    private final FinancierRepository financierRepository;
    private final FinancierMapper financierMapper;


    @Override
    public Financier save(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Financier can not be empty.");
        financier.validate();
        log.info("Financier before saving is {}", financier);
        FinancierEntity financierEntity = financierMapper.map(financier);
        FinancierEntity savedFinancierEntity = financierRepository.save(financierEntity);
        log.info("Financier saved to db: {}", savedFinancierEntity);
        return financierMapper.map(savedFinancierEntity);
    }
    @Override
    public Financier findById(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        FinancierProjection financierEntity = financierRepository.findByFinancierId(financierId)
                .orElseThrow(()-> new MeedlException(FinancierMessages.FINANCIER_NOT_FOUND.getMessage()));
        log.info("Financier found at the adapter level for view by financier id {}", financierEntity);
        Financier financier =  financierMapper.mapProjectionToFinancier(financierEntity);
        log.info("found financier {}",financier);
        return financier;
    }
    @Override
    public Financier findByFinancierId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Financier id cannot be empty");

        FinancierEntity financierEntity =
                financierRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(FinancierMessages.FINANCIER_NOT_FOUND.getMessage()));
        return financierMapper.map(financierEntity);
    }
    @Override
    public Financier findFinancierByOrganizationId(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, "Organization id is required to view financier details.");
        FinancierEntity foundFinancier = financierRepository.findByOrganizationId(organizationId)
                .orElseThrow(()-> new MeedlException(FinancierMessages.NOT_A_FINANCIER.getMessage()) );
        return financierMapper.map(foundFinancier);

    }

    @Override
    public Financier findCooperateFinancierById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Financier id cannot be empty");

        FinancierProjection financierProjection =
                financierRepository.findCooperateFinancierById(id);

        return financierMapper.mapProjectionToFinancier(financierProjection);
    }

    @Override
    public Financier findFinancierByUserId(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, "User id is required to view financier details.");
        log.info("Adapter level, finding financier by id {}", userId);
        FinancierEntity foundFinancier = financierRepository.findByUserIdentity_Id(userId)
                .orElseThrow(()-> new MeedlException(FinancierMessages.NOT_A_FINANCIER.getMessage()));
        return financierMapper.map(foundFinancier);
    }
    @Override
    public Financier findFinancierByEmail(String email) throws MeedlException {
        FinancierEntity foundFinancier = financierRepository.findByUserIdentity_Email(email)
                .orElseThrow(()-> new MeedlException("Financier not found with email : "+email ) );
        return financierMapper.map(foundFinancier);
    }

    @Override
    public Financier findByIdentity(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Identity id cannot be empty ");
        FinancierEntity financierEntity =
                financierRepository.findByIdentity(id);

        return financierMapper.map(financierEntity);
    }

    @Override
    public Financier findFinancierByCooperateStaffUserId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, UserMessages.INVALID_USER_ID.getMessage());
        FinancierProjection financierEntity = financierRepository.findByCooperateStaffUserId(id);
        return financierMapper.mapProjectionToFinancier(financierEntity);
    }

    @Override
    public void delete(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        log.info("Deleting financier with id {} ",financierId);
        financierRepository.deleteById(financierId);
    }

    @Override
    public Page<Financier> search(String name, Financier financier) throws MeedlException {
        MeedlValidator.validateDataElement(name, "Provide a valid name to search.");
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        log.info("Search financier parameters: name {}, page size {}, page number {}, financier type {}, activation status {}, investment vehicle id {}",
                name, financier.getPageSize(), financier.getPageNumber(), financier.getFinancierType(), financier.getActivationStatus(), financier.getInvestmentVehicleId());
        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        if (MeedlValidator.isEmptyCollection(financier.getActivationStatuses())){
            financier.setActivationStatuses(List.of());
        }
        Page<FinancierProjection> financierEntities =
                financierRepository.findByFinancierByNameFragmentOptionalInvestmentVehicleIdFinancierTypeActivationStatuses(
                        name, financier.getInvestmentVehicleId(), financier.getFinancierType(), financier.getActivationStatuses(), pageRequest);
        log.info("Financiers found on Entity search : {} {}", name, financierEntities );
        return financierEntities.map(financierMapper::mapProjectionToFinancier);
    }

    @Override
    public Financier completeKyc(Financier financier) throws MeedlException {
        log.info("Complete kyc of financier at adapter level ... ");
        MeedlValidator.validateObjectInstance(financier, "Kyc request cannot be empty");
        financier.validate();
        financier.validateKyc(financier.getFinancierType());
        financier.setAccreditationStatus(AccreditationStatus.VERIFIED);
        FinancierEntity financierToSave = financierMapper.map(financier);
        log.info("Financier to save for kyc: {}", financierToSave);
        FinancierEntity financierEntity = financierRepository.save(financierToSave);
        log.info("Financier completed KYC successfully : {} ", financierEntity);
        Financier kycCompletedFinancier = financierMapper.map(financierEntity);
        log.info("Kyc completed financier {}", kycCompletedFinancier);
        return kycCompletedFinancier;
    }

    @Override
    public Page<Financier> viewAllFinancier(Financier financier) throws MeedlException {
        log.info("Searching for all financier on the platform at adapter level.");
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());

        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize(), Sort.by(Sort.Direction.DESC, MeedlMessages.CREATED_AT.getMessage()));

        log.info("Page number: {}, page size: {}, financier type : {} view all financiers adapter level", financier.getPageNumber(), financier.getPageSize(), financier.getFinancierType());
        Page<FinancierProjection> financierEntities = financierRepository
                .findAllByFinancierTypeOrderByUserCreatedAt(financier.getFinancierType(), financier.getActivationStatuses(), pageRequest);

        log.info("Found financiers in db: {}", financierEntities);
        return financierEntities.map(financierMapper::mapProjectionToFinancier);
    }

}
