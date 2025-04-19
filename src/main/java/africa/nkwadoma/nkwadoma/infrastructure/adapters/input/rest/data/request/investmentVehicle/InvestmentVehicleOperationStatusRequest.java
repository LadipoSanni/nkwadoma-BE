package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;


import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleMode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvestmentVehicleOperationStatusRequest {

    private String investmentVehicleId;
    private InvestmentVehicleMode fundRaising;
    private InvestmentVehicleMode deployingStatus;
    private CouponDistributionStatus couponDistributionStatus;
    private InvestmentVehicleMode recollectionStatus;
}
