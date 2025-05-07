package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.PoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.PoliticallyExposedPersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PoliticallyExposedPersonMapper {
    PoliticallyExposedPersonEntity map(PoliticallyExposedPerson politicallyExposedPerson);

    PoliticallyExposedPerson map(PoliticallyExposedPersonEntity politicallyExposedPersonEntity);
}
