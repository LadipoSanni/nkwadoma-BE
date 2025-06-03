package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RepaymentHistory {
    private String id;
    private String firstName;
    private String lastName;
    private Loanee loanee;
    private LocalDateTime paymentDateTime;
    private BigDecimal amountPaid;
    private ModeOfPayment modeOfPayment;
    private BigDecimal totalAmountRepaid;
    private BigDecimal amountOutstanding;
    private Cohort cohort;
    private String actorId;
    private String loaneeId;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(paymentDateTime,"Payment date cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(amountPaid, "Amount paid cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(amountOutstanding, "Amount outstanding cannot be empty");
    }
}
