package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoanReferral {
    private String id;
    private Loanee loanee;
    private LoanReferralStatus loanReferralStatus;
    private int pageSize;
    private int pageNumber;

//    public int getPageSize() {
//        int defaultPageSize = BigInteger.TEN.intValue();
//        return this.pageSize == BigInteger.ZERO.intValue() ? defaultPageSize : this.pageSize;
//    }
}
