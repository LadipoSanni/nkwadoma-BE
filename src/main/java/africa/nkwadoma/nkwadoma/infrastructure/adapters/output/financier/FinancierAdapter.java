package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

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
    public Financier findFinancierByFinancierId(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        FinancierEntity financierEntity = financierRepository.findByFinancierId(financierId)
                .orElseThrow(()-> new MeedlException("Financier not found"));
        log.info("Financier found at the adapter level for view by financier id {}", financierEntity);
        return financierMapper.map(financierEntity);
    }

    @Override
    public Financier findFinancierByUserId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "User id is required to view financier details.");
        FinancierEntity foundFinancier = financierRepository.findByUserIdentity_Id(id)
                .orElseThrow(()-> new MeedlException("Apparently, you are not a financier. Contact admin.") );
        return financierMapper.map(foundFinancier);
    }
    @Override
    public Financier findFinancierByEmail(String email) throws MeedlException {
        FinancierEntity foundFinancier = financierRepository.findByUserIdentity_Email(email)
                .orElseThrow(()-> new MeedlException("Financier with this email not found") );
        return financierMapper.map(foundFinancier);
    }


    @Override
    public void delete(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        financierRepository.deleteById(financierId);
    }

    @Override
    public Page<Financier> search(String name, String investmentVehicleId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateDataElement(name, "Provide a valid name to search.");
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<FinancierEntity> financierEntities = financierRepository.findByNameFragmentAndOptionalVehicleId(name, investmentVehicleId, pageRequest);
        log.info("Financiers found with name: {} {}", name, financierEntities );
        return financierEntities.map(financierMapper::map);
    }

    @Override
    public Financier completeKyc(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Kyc request cannot be empty");
        financier.validate();
        financier.validateKyc();
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

        log.info("Page number: {}, page size: {}, financier type : {}", financier.getPageNumber(), financier.getPageSize(), financier.getFinancierType());
        Page<FinancierEntity> financierEntities = financierRepository
                .findAllByFinancierTypeOrderByUserCreatedAt(financier.getFinancierType(), financier.getActivationStatus(), pageRequest);

        log.info("Found financiers in db: {}", financierEntities);
        return financierEntities.map(financierMapper::map);
    }


}
