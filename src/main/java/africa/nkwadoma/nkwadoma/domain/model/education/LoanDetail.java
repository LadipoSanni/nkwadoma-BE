package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import org.apache.commons.validator.routines.EmailValidator;

import java.math.BigDecimal;

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
