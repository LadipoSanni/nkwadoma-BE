package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.OperationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class VehicleOperationEntity {
    @Id
    @UuidGenerator
    private String id;
    private CouponDistributionStatus couponDistributionStatus;
    @OneToOne
    private CouponDistributionEntity couponDistribution;
    private InvestmentVehicleMode fundRaisingStatus;
    private InvestmentVehicleMode deployingStatus;
    private OperationStatus operationStatus;
}
