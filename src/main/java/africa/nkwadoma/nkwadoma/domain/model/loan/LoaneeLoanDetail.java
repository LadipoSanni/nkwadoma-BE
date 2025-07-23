package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoaneeLoanDetail {
    private String id;
    private BigDecimal tuitionAmount;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private BigDecimal amountReceived = BigDecimal.ZERO;
    private BigDecimal amountApproved;
    private BigDecimal amountRepaid;
    private BigDecimal amountOutstanding;
    private double interestRate;
    private BigDecimal interestIncurred = BigDecimal.ZERO;

    public void validate() throws MeedlException {
        MeedlValidator.validateNegativeAmount(initialDeposit,"Initial deposit");
    }
}
