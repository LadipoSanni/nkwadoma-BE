package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanOfferMessages {

    LOAN_OFFER_NOT_FOUND("Loan offer not found"),
    INVALID_LOAN_OFFER_ID("Please provide a valid loan offer id"),
    LOAN_OFFER_CANNOT_BE_EMPTY("Loan offer cannot be empty or null"),
    LOAN_OFFER_IS_NOT_ASSIGNED_TO_LOANEE("Loan offer is not assigned to loanee");
    private final String message;

    LoanOfferMessages(String message) {
        this.message = message;
    }
}
