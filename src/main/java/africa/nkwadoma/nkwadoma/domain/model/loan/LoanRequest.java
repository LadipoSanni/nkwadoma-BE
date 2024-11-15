package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import lombok.*;

import java.math.*;
import java.time.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    private String id;
    private String referredBy;
    private BigDecimal loanAmountRequested;
    private LocalDateTime dateTimeApproved;
    private String reasonForDecliningLoanRequest;
    private LoanRequestStatus status;
    private Loanee loanee;
}
