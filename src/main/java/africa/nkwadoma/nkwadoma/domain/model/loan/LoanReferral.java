package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class LoanReferral {

    private String id;
    private Loanee loanee;
    @Enumerated(EnumType.STRING)
    private LoanReferralStatus loanReferralStatus;
    private int numberOfReferrals;
}
