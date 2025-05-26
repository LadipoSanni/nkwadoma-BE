package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.math.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDetail {

    private String id;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;
    private BigDecimal totalOutstanding;
    private Double repaymentPercentage;
    private Double debtPercentage;
    private BigDecimal totalInterestIncurred;
    private BigDecimal monthlyExpected;
    private BigDecimal lastMonthActual;
    private Double creditWorthinessRating;
    private LocalDateTime loanStartDate;


    public void validate() throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(totalAmountDisbursed);
        MeedlValidator.validateBigDecimalDataElement(totalAmountRepaid);
        MeedlValidator.validateBigDecimalDataElement(totalOutstanding);
        MeedlValidator.validateBigDecimalDataElement(totalInterestIncurred);
        MeedlValidator.validateBigDecimalDataElement(monthlyExpected);
        MeedlValidator.validateBigDecimalDataElement(lastMonthActual);
        MeedlValidator.validateDoubleDataElement(repaymentPercentage);
        MeedlValidator.validateDoubleDataElement(debtPercentage);
    }


}
