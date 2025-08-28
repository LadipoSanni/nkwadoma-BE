package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class AllLoanReferralResponse {

    private String referralId;
    private String firstName;
    private String lastName;
    private BigDecimal initialDeposit;
    private BigDecimal amountReferred;
    private LocalDateTime referralDate;
}
