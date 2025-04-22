package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.BeneficialOwnerOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.BeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.BeneficialOwnerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BeneficialOwnerAdapter implements BeneficialOwnerOutputPort {
    private final BeneficialOwnerRepository beneficialOwnerRepository;
    private final BeneficialOwnerMapper beneficialOwnerMapper;
    @Override
    public BeneficialOwner save(BeneficialOwner beneficialOwner) throws MeedlException {
        MeedlValidator.validateObjectInstance(beneficialOwner, "Beneficial owner can not be empty");
        beneficialOwner.validate();
        log.info("Beneficial owner to save in beneficial owner adapter : {}", beneficialOwner);
        BeneficialOwnerEntity beneficialOwnerEntityToSave = beneficialOwnerMapper.toBeneficialOwnerEntity(beneficialOwner);
        log.info("Beneficial owner to save mapped : {}",beneficialOwnerEntityToSave);
        BeneficialOwnerEntity beneficialOwnerEntity = beneficialOwnerRepository.save(beneficialOwnerEntityToSave);
        log.info("Beneficial owner saved successfully {}", beneficialOwnerEntity);
        return beneficialOwnerMapper.toBeneficialOwner(beneficialOwnerEntity);
    }

    @Override
    public BeneficialOwner findById(String beneficialOwnerId) throws MeedlException {
        MeedlValidator.validateUUID(beneficialOwnerId, "Beneficial owner id cannot be empty");
        BeneficialOwnerEntity beneficialOwnerEntity = beneficialOwnerRepository.findById(beneficialOwnerId)
                .orElseThrow(() -> new MeedlException("Beneficial owner not found"));
        return beneficialOwnerMapper.toBeneficialOwner(beneficialOwnerEntity);
    }

    @Override
    public void deleteById(String beneficialOwnerId) throws MeedlException {
        MeedlValidator.validateUUID(beneficialOwnerId, "Beneficial owner id cannot be empty");
        beneficialOwnerRepository.deleteById(beneficialOwnerId);
    }
}
