package africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.VehicleOperation;

public interface VehicleOperationOutputPort {
    VehicleOperation save(VehicleOperation vehicleOperation) throws MeedlException;

    void deleteById(String vehicleOperationId) throws MeedlException;

    VehicleOperation changeOperationStatuses(VehicleOperation vehicleOperation) throws MeedlException;
}
