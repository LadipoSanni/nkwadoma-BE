package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanProductDisbursementRuleMessages {

    EMPTY_LOAN_PRODUCT_DISBURSEMENT_RULE ("Loan product disbursement rule cannot be empty");


    private final String message;
    LoanProductDisbursementRuleMessages(String message) {
        this.message = message;
    }
}
