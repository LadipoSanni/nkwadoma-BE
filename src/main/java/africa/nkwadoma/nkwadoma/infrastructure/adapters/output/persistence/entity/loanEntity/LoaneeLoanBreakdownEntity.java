package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class LoaneeLoanBreakdownEntity {

    @Id
    private String loaneeLoanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount = BigDecimal.ZERO;
    private String currency;
    @ManyToOne
    private LoaneeEntity loanee;
}
