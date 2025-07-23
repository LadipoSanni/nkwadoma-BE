package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RepaymentHistoryEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private LoaneeEntity loanee;
    private LocalDateTime paymentDateTime;
    private String cohortId;
    private BigDecimal amountPaid;
    private BigDecimal totalAmountRepaid;
    private BigDecimal amountOutstanding;
    @Enumerated(EnumType.STRING)
    private ModeOfPayment modeOfPayment;
    private BigDecimal interestIncurred;
}
