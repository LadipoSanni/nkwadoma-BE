package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.OperationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.VehicleOperation;

public interface VehicleOperationOutputPort {
    VehicleOperation save(VehicleOperation vehicleOperation) throws MeedlException;

    void deleteById(String vehicleOperationId) throws MeedlException;

    VehicleOperation changeOperationStatuses(VehicleOperation vehicleOperation) throws MeedlException;
}
