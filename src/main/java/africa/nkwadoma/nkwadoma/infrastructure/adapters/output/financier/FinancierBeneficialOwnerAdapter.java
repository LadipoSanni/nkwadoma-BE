package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierBeneficialOwnerOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierBeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierBeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierBeneficialOwnerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class FinancierBeneficialOwnerAdapter implements FinancierBeneficialOwnerOutputPort {
    private final FinancierBeneficialOwnerRepository financierBeneficialOwnerRepository;
    private final FinancierBeneficialOwnerMapper financierBeneficialOwnerMapper;
    @Override
    public FinancierBeneficialOwner save(FinancierBeneficialOwner financierBeneficialOwner) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierBeneficialOwner, "Financier beneficial owner can not be empty");
        financierBeneficialOwner.validate();
        log.info("Financier beneficial owner to save in beneficial owner adapter : {}", financierBeneficialOwner);
        FinancierBeneficialOwnerEntity financierBeneficialOwnerEntityToSave = financierBeneficialOwnerMapper.toFinancierBeneficialOwnerEntity(financierBeneficialOwner);
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
}
