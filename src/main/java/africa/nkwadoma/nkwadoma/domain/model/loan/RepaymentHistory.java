package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class RepaymentHistory {
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private String modeOfPayment;
}
