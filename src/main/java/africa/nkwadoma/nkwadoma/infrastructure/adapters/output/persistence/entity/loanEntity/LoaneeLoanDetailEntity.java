package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
public class LoaneeLoanDetailEntity {

    @Id
    @UuidGenerator
    private String id;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
}
