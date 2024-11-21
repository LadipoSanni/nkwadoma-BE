package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanRequestStatus;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class LoanOfferResponse {

    private String id;
    private String loanRequestId;
//    private String referredBy;
//    private BigDecimal loanAmountRequested;
//    private LoanReferralStatus loanReferralStatus;
//    private LocalDateTime dateTimeApproved;
//    private LoanRequestStatus loanRequestStatus;
//    private LoanOfferStatus loanOfferStatus;
    private LoaneeResponse loanee;
    private LocalDateTime dateTimeOffered;
}
