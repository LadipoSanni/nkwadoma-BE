package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.VehicleOperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.OperationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.VehicleOperation;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.VehicleOperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.VehicleOperationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.VehicleOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class VehicleOperationAdapter implements VehicleOperationOutputPort {

    private final VehicleOperationRepository vehicleOperationRepository;
    private final VehicleOperationMapper vehicleOperationMapper;

    @Override
    public VehicleOperation save(VehicleOperation vehicleOperation) throws MeedlException {
        MeedlValidator.validateObjectInstance(vehicleOperation,"Vehicle operation cannot be empty");
        VehicleOperationEntity vehicleOperationEntity =
                vehicleOperationMapper.toVehicleOperationEntity(vehicleOperation);
        vehicleOperationEntity = vehicleOperationRepository.save(vehicleOperationEntity);
        return vehicleOperationMapper.toVehicleOperation(vehicleOperationEntity);
    }

    @Override
    public void deleteById(String vehicleOperationId) throws MeedlException {
        MeedlValidator.validateUUID(vehicleOperationId,"Vehicle operation id cannot be empty");
        vehicleOperationRepository.deleteById(vehicleOperationId);
    }

    @Override
    public VehicleOperation changeOperationStatuses(VehicleOperation vehicleOperation) throws MeedlException {
        vehicleOperation.changeOperationStatusesValidation(vehicleOperation);
        VehicleOperationEntity vehicleOperationEntity =
                vehicleOperationRepository.findById(vehicleOperation.getId()).orElseThrow(() ->
                        new MeedlException("Vehicle operation not found"));
        vehicleOperationMapper.updateVehicleOperation(vehicleOperationEntity,vehicleOperation);
        vehicleOperationEntity = vehicleOperationRepository.save(vehicleOperationEntity);
        return vehicleOperationMapper.toVehicleOperation(vehicleOperationEntity);
    }
}
