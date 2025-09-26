package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierPoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierPoliticallyExposedPersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {FinancierMapper.class, PoliticallyExposedPersonMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierPoliticallyExposedPersonMapper {
    FinancierPoliticallyExposedPerson map(FinancierPoliticallyExposedPersonEntity financierPoliticallyExposedPersonEntity);
}
