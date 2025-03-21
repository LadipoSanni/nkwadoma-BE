package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
public class CapitalDistributionEntity {
    @Id
    @UuidGenerator
    private String id;
    private int due;
    private BigDecimal totalCapitalPaidOut;
}
