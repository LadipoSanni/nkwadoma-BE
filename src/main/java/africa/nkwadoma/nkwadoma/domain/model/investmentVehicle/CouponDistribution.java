package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class CouponDistribution {

    private String id;
    private int due;
    private int paid;
    private LocalDateTime lastDatePaid;
    private LocalDateTime lastDateDue;
}
