package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;


import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.VehicleClosureOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.VehicleClosure;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.VehicleClosureMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.VehicleClosureEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle.VehicleClosureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class VehicleClosureAdapter implements VehicleClosureOutputPort {


    private final VehicleClosureMapper vehicleClosureMapper;
    private final VehicleClosureRepository vehicleClosureRepository;

    @Override
    public VehicleClosure save(VehicleClosure vehicleClosure) throws MeedlException {
        MeedlValidator.validateObjectInstance(vehicleClosure,"Vehicle closure cannot be empty");
        VehicleClosureEntity vehicleClosureEntity =
                vehicleClosureMapper.toVehicleClosureEntity(vehicleClosure);
        vehicleClosureEntity = vehicleClosureRepository.save(vehicleClosureEntity);
        return vehicleClosureMapper.toVehicleClosure(vehicleClosureEntity);
    }

    @Override
    public void deleteById(String vehicleId) throws MeedlException {
        MeedlValidator.validateUUID(vehicleId,"Vehicle closure id cannot be empty");
        vehicleClosureRepository.deleteById(vehicleId);
    }
}
