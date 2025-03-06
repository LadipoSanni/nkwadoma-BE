package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class CapitalDistributionEntity {
    @Id
    private String id;
    private int due;
    private BigDecimal totalCapitalPaidOut;
}
