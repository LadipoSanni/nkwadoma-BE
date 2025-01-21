package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class LoanOffer {
    private String id;
    private String loanRequestId;
    private LocalDate startDate;
    private LoanOfferStatus loanOfferStatus;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanRequest loanRequest;
    private Loanee loanee;
    private List<LoaneeLoanBreakdown> loaneeBreakdown;
    private LoanProduct loanProduct;
    private LocalDateTime dateTimeOffered;
    private String loaneeId;
    private String userId;
    private LocalDateTime dateTimeAccepted;
    private LoanDecision loaneeResponse;
    private UserIdentity userIdentity;
    private NextOfKin nextOfKin;



    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(loanRequest.getId(),"LoanRequest Id");
    }

    public void validateForAcceptOffer() throws MeedlException {
        MeedlValidator.validateUUID(userId);
        MeedlValidator.validateUUID(id,"LoanOffer Id");
    }
}
