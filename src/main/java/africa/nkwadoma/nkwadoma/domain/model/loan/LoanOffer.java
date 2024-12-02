package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferResponse;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class LoanOffer {
    private String id;
    private LoanRequest loanRequest;
    private LoanOfferStatus loanOfferStatus;
    private Loanee loanee;
    private LoanProduct loanProduct;
    private LocalDateTime dateTimeOffered;
    private String loaneeId;
    private LocalDateTime dateTimeAccepted;
    private LoanOfferResponse loaneeResponse;



    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(loanRequest.getId());
    }

    public void validateForAcceptOffer() throws MeedlException {
        MeedlValidator.validateUUID(loaneeId);
        MeedlValidator.validateUUID(id);
    }
}
