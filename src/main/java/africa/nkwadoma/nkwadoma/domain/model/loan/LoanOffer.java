package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanRequestStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanOffer {
    private String id;
    private LoanRequest loanRequest;
    private LoanOfferStatus loanOfferStatus;
    private Loanee loanee;
    private LocalDateTime dateTimeOffered;

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(loanRequest.getId());
    }

}
