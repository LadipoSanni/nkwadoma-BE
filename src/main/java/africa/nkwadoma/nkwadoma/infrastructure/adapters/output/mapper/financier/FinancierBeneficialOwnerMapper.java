package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierBeneficialOwnerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierBeneficialOwnerMapper {
    FinancierBeneficialOwnerEntity toFinancierBeneficialOwnerEntity(FinancierBeneficialOwner financierBeneficialOwner);

    FinancierBeneficialOwner toFinancierBeneficialOwner(FinancierBeneficialOwnerEntity financierBeneficialOwnerEntity);
}
