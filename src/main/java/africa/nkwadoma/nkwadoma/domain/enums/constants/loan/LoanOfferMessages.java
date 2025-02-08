package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanOfferMessages {

    LOAN_OFFER_NOT_FOUND("Loan offer not found"),
    LOAN_OFFER_IS_NOT_ASSIGNED_TO_LOANEE("Loan offer is not assigned to loanee");
    private final String message;

    LoanOfferMessages(String message) {
        this.message = message;
    }
}
