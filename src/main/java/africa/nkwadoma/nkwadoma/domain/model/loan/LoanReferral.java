package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import lombok.*;

import java.math.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
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
