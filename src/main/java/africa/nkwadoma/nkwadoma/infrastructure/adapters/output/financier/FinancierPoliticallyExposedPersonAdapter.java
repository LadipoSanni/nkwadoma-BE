package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierPoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierPoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.BeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierBeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierBeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierBeneficialOwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FinancierPoliticallyExposedPersonAdapter implements FinancierPoliticallyExposedPersonOutputPort {
    private final FinancierBeneficialOwnerRepository financierBeneficialOwnerRepository;
    private final FinancierBeneficialOwnerMapper financierBeneficialOwnerMapper;
    private final BeneficialOwnerMapper beneficialOwnerMapper;
    private final FinancierMapper financierMapper;
    @Override
    public FinancierPoliticallyExposedPerson save(FinancierPoliticallyExposedPerson financierPoliticallyExposedPerson) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierPoliticallyExposedPerson, "Financier beneficial owner can not be empty");
        financierPoliticallyExposedPerson.validate();
        log.info("Financier  to save in beneficial owner adapter : {}", financierPoliticallyExposedPerson);
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntityToSave = mapToFinancierBeneficialOwnerEntity(financierBeneficialOwner);

        log.info("Financier beneficial owner to save mapped : {}",financierBeneficialOwnerEntityToSave);
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity = financierBeneficialOwnerRepository.save(financierBeneficialOwnerEntityToSave);
        log.info("Financier beneficial owner saved successfully {}", financierBeneficialOwnerEntity);
        return financierBeneficialOwnerMapper.toFinancierBeneficialOwner(financierBeneficialOwnerEntity);
    }

    @Override
    public FinancierPoliticallyExposedPerson findById(String politicallyExposedPersonId) throws MeedlException {
        MeedlValidator.validateUUID(politicallyExposedPersonId, "Financier beneficial owner id cannot be empty");
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity= financierBeneficialOwnerRepository.findById(beneficialOwnerId)
                .orElseThrow(() -> new MeedlException("Financier beneficial owner not found"));
        return financierBeneficialOwnerMapper.toFinancierBeneficialOwner(financierBeneficialOwnerEntity);
    }

    @Override
    public void deleteById(String politicallyExposedPersonId) throws MeedlException {
        MeedlValidator.validateUUID(politicallyExposedPersonId, "Financier beneficial owner id cannot be empty");
        financierBeneficialOwnerRepository.deleteById(beneficialOwnerId);
    }

    @Override
    public List<BeneficialOwner> findAllBeneficialOwner(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        List<BeneficialOwnerEntity> beneficialOwnerEntities = financierBeneficialOwnerRepository.findBeneficialOwnersByFinancierId(financierId);
        return beneficialOwnerMapper.toBeneficialOwners(beneficialOwnerEntities);
    }

    @Override
    public List<FinancierBeneficialOwner> findAllByFinancierId(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        List<FinancierBeneficialOwnerEntity> financierBeneficialOwnerEntities = financierBeneficialOwnerRepository.findAllByFinancierEntity_Id(financierId);
        log.info("Financier beneficial owner {}", financierBeneficialOwnerEntities);
        return financierBeneficialOwnerEntities
                .stream().map(this::mapToFinancierBeneficialOwner)
                .toList();
//        return financierBeneficialOwnerMapper.toFinancierBeneficialOwners(financierBeneficialOwnerEntities);
    }
    private FinancierBeneficialOwnerEntity mapToFinancierBeneficialOwnerEntity(FinancierBeneficialOwner financierBeneficialOwner) {
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity = new FinancierBeneficialOwnerEntity();
        financierBeneficialOwnerEntity.setBeneficialOwnerEntity(beneficialOwnerMapper.toBeneficialOwnerEntity(financierBeneficialOwner.getBeneficialOwner()));
        financierBeneficialOwnerEntity.setFinancierEntity(financierMapper.map(financierBeneficialOwner.getFinancier()));
        return financierBeneficialOwnerEntity;
    }
    private FinancierBeneficialOwner mapToFinancierBeneficialOwner(FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity) {
        FinancierBeneficialOwner financierBeneficialOwner = new FinancierBeneficialOwner();
        financierBeneficialOwner.setId(financierBeneficialOwnerEntity.getId());
        financierBeneficialOwner.setBeneficialOwner(beneficialOwnerMapper.toBeneficialOwner(financierBeneficialOwnerEntity.getBeneficialOwnerEntity()));
        financierBeneficialOwner.setFinancier(financierMapper.map(financierBeneficialOwnerEntity.getFinancierEntity()));
        return financierBeneficialOwner;
    }
}
