package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CooperationMapper {
    Cooperation toCooperation(CooperationEntity cooperationEntity);

    CooperationEntity toCooperationEntity(Cooperation cooperation);
}
