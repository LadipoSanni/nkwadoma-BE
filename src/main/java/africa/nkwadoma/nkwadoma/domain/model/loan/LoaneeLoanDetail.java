package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private BigDecimal amountRequested ;
    private BigDecimal amountReceived = BigDecimal.ZERO;
    private BigDecimal amountApproved = BigDecimal.ZERO;
    private BigDecimal amountRepaid = BigDecimal.ZERO;
    private BigDecimal amountOutstanding = BigDecimal.ZERO;
    private double interestRate;
    private BigDecimal interestIncurred = BigDecimal.ZERO;

    private LocalDateTime loanStartDate;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public void validate() throws MeedlException {
        MeedlValidator.validateNegativeAmount(initialDeposit,"Initial deposit");
    }
}
