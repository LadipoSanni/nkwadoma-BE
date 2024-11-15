package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
public class LoaneeLoanDetailEntity {
    @Id
    @UuidGenerator
    private String id;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    @OneToMany(fetch = FetchType.EAGER)
    private List<LoanBreakdownEntity> loanBreakdown;
}
