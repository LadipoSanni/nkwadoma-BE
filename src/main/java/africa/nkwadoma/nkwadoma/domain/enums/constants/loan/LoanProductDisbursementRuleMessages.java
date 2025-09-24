package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanProductDisbursementRuleMessages {

    LOAN_PRODUCT_DISBURSEMENT_RULE_NOT_FOUND("Loan product disbursement rule not found"),
    INVALID_LOAN_PRODUCT_DISBURSEMENT_RULE_ID("Disbursement rule id cannot be invalid"),
    EMPTY_LOAN_PRODUCT_DISBURSEMENT_RULE ("Loan product disbursement rule cannot be empty");

    private final String message;
    LoanProductDisbursementRuleMessages(String message) {
        this.message = message;
    }
}
