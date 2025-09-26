package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.VehicleClosure;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.VehicleClosureEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserIdentityMapper.class, NextOfKinMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleClosureMapper {
    VehicleClosureEntity toVehicleClosureEntity(VehicleClosure vehicleClosure);

    VehicleClosure toVehicleClosure(VehicleClosureEntity vehicleClosureEntity);
}
