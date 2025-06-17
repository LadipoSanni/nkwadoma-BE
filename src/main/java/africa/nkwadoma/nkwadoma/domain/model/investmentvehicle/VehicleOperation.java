package africa.nkwadoma.nkwadoma.domain.model.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.OperationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class VehicleOperation {

    private String id;
    private CouponDistributionStatus couponDistributionStatus;
    private CouponDistribution couponDistribution;
    private InvestmentVehicleMode fundRaisingStatus;
    private InvestmentVehicleMode deployingStatus;
    private OperationStatus operationStatus;

    public void changeOperationStatusesValidation(VehicleOperation vehicleOperation) throws MeedlException {
        MeedlValidator.validateUUID(id,"Vehicle operation id cannot be empty");
        MeedlValidator.validateIncorrectStatus(vehicleOperation.getCouponDistributionStatus(),CouponDistributionStatus.values());
        MeedlValidator.validateIncorrectStatus(vehicleOperation.getDeployingStatus(),InvestmentVehicleMode.values());
        MeedlValidator.validateIncorrectStatus(vehicleOperation.getOperationStatus(),OperationStatus.values());
        MeedlValidator.validateIncorrectStatus(vehicleOperation.getFundRaisingStatus(),InvestmentVehicleMode.values());
    }


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(couponDistribution,"Coupon distribution cannot be empty");
        MeedlValidator.validateObjectInstance(fundRaisingStatus,"Fund raising status cannot be empty");
        MeedlValidator.validateObjectInstance(deployingStatus,"Deploying status cannot be empty");
        MeedlValidator.validateObjectInstance(operationStatus,"Operation status cannot be empty");
        MeedlValidator.validateObjectInstance(couponDistributionStatus,"Coupon distribution status cannot be empty");
    }

    public void validateFundraisingAndDeployingStatus() throws MeedlException {
        if (fundRaisingStatus != null && deployingStatus != null) {
            throw new MeedlException("Fundraising status and deploying status cannot be the set at the same time");
        }
        if (fundRaisingStatus == null && deployingStatus == null) {
            throw new MeedlException("Both fundraising status and deploying status cannot be empty");
        }

    }
}
