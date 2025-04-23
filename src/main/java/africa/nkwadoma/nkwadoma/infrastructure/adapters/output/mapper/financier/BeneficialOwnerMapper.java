package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BeneficialOwnerMapper {
    BeneficialOwnerEntity toBeneficialOwnerEntity(BeneficialOwner beneficialOwner);

    BeneficialOwner toBeneficialOwner(BeneficialOwnerEntity beneficialOwnerEntity);

    List<BeneficialOwner> toBeneficialOwners(List<BeneficialOwnerEntity> beneficialOwnerEntities);
}
