package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",uses = {UserIdentityMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CooperationMapper {
    Cooperation toCooperation(CooperationEntity cooperationEntity);

    CooperationEntity toCooperationEntity(Cooperation cooperation);
}
