package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio.DemographyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DemographyMapper {
    DemographyEntity toDemographyEntity(Demography demography);

    Demography toDemography(DemographyEntity demographyEntity);
}
