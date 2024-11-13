package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class LoanReferral {
    private String id;
    private Loanee loanee;
    private LoanReferralStatus loanReferralStatus;
}
