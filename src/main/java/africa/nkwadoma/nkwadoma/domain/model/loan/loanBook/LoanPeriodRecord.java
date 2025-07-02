package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoanPeriodRecord {
        private BigDecimal loanAmountOutstanding;
        private int daysHeld;
}
