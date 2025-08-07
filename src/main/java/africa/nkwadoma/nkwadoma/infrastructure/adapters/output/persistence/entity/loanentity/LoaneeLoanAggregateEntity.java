package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;


import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
public class LoaneeLoanAggregateEntity {

    @Id
    @UuidGenerator
    private String id;
    private BigDecimal historicalDebt;
    private BigDecimal totalAmountOutstanding;
    private int numberOfLoans;
    @OneToOne
    private LoaneeEntity loanee;
}
