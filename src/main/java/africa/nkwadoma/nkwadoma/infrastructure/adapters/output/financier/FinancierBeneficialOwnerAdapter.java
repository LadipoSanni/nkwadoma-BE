package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierBeneficialOwnerOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.BeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierBeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierBeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierBeneficialOwnerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class FinancierBeneficialOwnerAdapter implements FinancierBeneficialOwnerOutputPort {
    private final FinancierBeneficialOwnerRepository financierBeneficialOwnerRepository;
    private final FinancierBeneficialOwnerMapper financierBeneficialOwnerMapper;
    private final BeneficialOwnerMapper beneficialOwnerMapper;
    private final FinancierMapper financierMapper;
    @Override
    public FinancierBeneficialOwner save(FinancierBeneficialOwner financierBeneficialOwner) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierBeneficialOwner, "Financier beneficial owner can not be empty");
        financierBeneficialOwner.validate();
        log.info("Financier beneficial owner to save in beneficial owner adapter : {}", financierBeneficialOwner);
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntityToSave = mapToFinancierBeneficialOwnerEntity(financierBeneficialOwner);

        log.info("Financier beneficial owner to save mapped : {}",financierBeneficialOwnerEntityToSave);
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity = financierBeneficialOwnerRepository.save(financierBeneficialOwnerEntityToSave);
        log.info("Financier beneficial owner saved successfully {}", financierBeneficialOwnerEntity);
        return financierBeneficialOwnerMapper.toFinancierBeneficialOwner(financierBeneficialOwnerEntity);
    }

    @Override
    public FinancierBeneficialOwner findById(String beneficialOwnerId) throws MeedlException {
        MeedlValidator.validateUUID(beneficialOwnerId, "Financier beneficial owner id cannot be empty");
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity= financierBeneficialOwnerRepository.findById(beneficialOwnerId)
                .orElseThrow(() -> new MeedlException("Financier beneficial owner not found"));
        return financierBeneficialOwnerMapper.toFinancierBeneficialOwner(financierBeneficialOwnerEntity);
    }

    @Override
    public void deleteById(String beneficialOwnerId) throws MeedlException {
        MeedlValidator.validateUUID(beneficialOwnerId, "Financier beneficial owner id cannot be empty");
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
