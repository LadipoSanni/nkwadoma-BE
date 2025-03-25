package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
@Builder
public class CapitalDistribution {

    private String id;
    private int due;
    private BigDecimal totalCapitalPaidOut;
}
