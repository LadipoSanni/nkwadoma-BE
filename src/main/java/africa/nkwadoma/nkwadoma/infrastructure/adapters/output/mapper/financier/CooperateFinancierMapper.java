package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.CooperateFinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.CooperateFinancierProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CooperateFinancierMapper {
    CooperateFinancierEntity toCooperateFinancierEntity(CooperateFinancier cooperateFinancier);

    CooperateFinancier toCooperateFinancier(CooperateFinancierEntity cooperateFinancierEntity);


    @Mapping(target = "activationStatus", source = "status")

    CooperateFinancier mapCooperateFinancierProjectionToCooperateFinancier(CooperateFinancierProjection cooperateFinancierProjection);
}
