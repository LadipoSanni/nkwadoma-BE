package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.model.education.*;
import lombok.*;

import java.math.*;
import java.util.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoaneeLoanDetail {
    private String id;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private List<LoanBreakdown> loanBreakdown = new ArrayList<>();
}
