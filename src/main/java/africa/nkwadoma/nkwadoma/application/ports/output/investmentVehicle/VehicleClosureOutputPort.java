package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.VehicleClosure;

public interface VehicleClosureOutputPort {

    VehicleClosure save(VehicleClosure vehicleClosure) throws MeedlException;

    void deleteById(String vehicleId) throws MeedlException;
}
