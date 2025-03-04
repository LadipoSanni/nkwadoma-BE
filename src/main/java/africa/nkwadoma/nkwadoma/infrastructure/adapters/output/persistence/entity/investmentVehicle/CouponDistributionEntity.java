package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class CouponDistributionEntity {
    @Id
    private String id;
    private int due;
    private int paid;
    private LocalDateTime lastDatePaid;
    private LocalDateTime lastDateDue;
}
