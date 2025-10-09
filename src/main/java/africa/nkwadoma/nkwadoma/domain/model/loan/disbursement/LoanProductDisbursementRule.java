package africa.nkwadoma.nkwadoma.domain.model.loan.disbursement;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoanProductDisbursementRule {
    private String id;
    private LoanProduct loanProduct;
    private DisbursementRule disbursementRule;
    public void validate() {

    }
}
