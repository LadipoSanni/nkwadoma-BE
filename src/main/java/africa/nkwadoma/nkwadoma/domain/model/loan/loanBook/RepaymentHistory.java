package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RepaymentHistory {
    private String firstName;
    private String lastName;
    private UserIdentity userIdentity;
    private String paymentDate;
    private BigDecimal amountPaid;
    private ModeOfPayment modeOfPayment;

    private Cohort cohort;

    public void validate() {
    }
}
