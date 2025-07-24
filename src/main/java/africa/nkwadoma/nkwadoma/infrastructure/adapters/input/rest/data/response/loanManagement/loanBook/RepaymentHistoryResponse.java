package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook;


import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class RepaymentHistoryResponse {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDateTime paymentDateTime;
    private BigDecimal amountPaid;
    private ModeOfPayment modeOfPayment;
    private BigDecimal totalAmountRepaid;
    private BigDecimal amountOutstanding;
    private BigDecimal interestIncurred;
}
