package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate paymentDate;
    private Integer month;
    private Integer year;
    private BigDecimal amountPaid;
    private ModeOfPayment modeOfPayment;
    private BigDecimal totalAmountRepaid;
    private BigDecimal amountOutstanding;
    private BigDecimal principalPayment;
    private BigDecimal interestIncurred;
    private Cohort cohort;
    private String actorId;
    private String loaneeId;
    private String loaneeName;
    private Integer firstYear;
    private Integer lastYear;
    private String loanId;
    private int tenor;
    private int moratorium;

    public void validate() throws MeedlException {
//        MeedlValidator.validateObjectInstance(paymentDateTime,"Payment date cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(amountPaid, "Amount paid cannot be empty");
//        MeedlValidator.validateBigDecimalDataElement(amountOutstanding, "Amount outstanding cannot be empty");
    }
}
