package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;


import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.VehicleOperation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.VehicleOperationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserIdentityMapper.class, NextOfKinMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleOperationMapper {
    VehicleOperationEntity toVehicleOperationEntity(VehicleOperation vehicleOperation);

    VehicleOperation toVehicleOperation(VehicleOperationEntity vehicleOperationEntity);

    void updateVehicleOperation(@MappingTarget VehicleOperationEntity vehicleOperationEntity, VehicleOperation vehicleOperation);

    void updateExistiingVehicleOperation(@MappingTarget VehicleOperation vehicleOperation, VehicleOperation vehicleOperation1);
}
