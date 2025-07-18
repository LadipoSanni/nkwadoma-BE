package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Entity
public class LoaneeLoanDetailEntity {
    @Id
    @UuidGenerator
    private String id;
    private BigDecimal tuitionAmount;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private BigDecimal amountReceived;
    private BigDecimal amountRepaid;
    private BigDecimal amountOutstanding;
    private double interestRate;
    private double interestIncurred;
}
