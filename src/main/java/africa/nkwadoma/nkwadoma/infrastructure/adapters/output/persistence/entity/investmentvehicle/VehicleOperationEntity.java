package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.CouponDistributionStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.OperationStatus;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private CouponDistributionStatus couponDistributionStatus;
    @OneToOne
    private CouponDistributionEntity couponDistribution;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleMode fundRaisingStatus;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleMode deployingStatus;
    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;
}
