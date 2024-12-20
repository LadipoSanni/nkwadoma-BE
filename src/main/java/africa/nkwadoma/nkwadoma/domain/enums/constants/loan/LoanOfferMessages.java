package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanOfferMessages {

    LOAN_OFFER_NOT_FOUND("Loan Offer Not Found"),
    LOAN_OFFER_IS_NOT_ASSIGNED_TO_LOANEE("Loan Offer Is Not Assigned To Loanee");
    private final String message;

    LoanOfferMessages(String message) {
        this.message = message;
    }
}
