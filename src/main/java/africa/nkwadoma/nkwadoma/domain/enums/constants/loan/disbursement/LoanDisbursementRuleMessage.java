package africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement;

import lombok.Getter;

@Getter
public enum LoanDisbursementRuleMessage {
    EMPTY_LOAN_DISBURSEMENT_RULE("Loan disbursement rule cannot be empty"),
    INVALID_LOAN_DISBURSEMENT_RULE_NAME("Loan disbursement rule name cannot be empty"),
    LOAN_DISBURSEMENT_RULE_NOT_FOUND("Loan disbursement rule not found"),
    INVALID_LOAN_DISBURSEMENT_RULE_ACTIVATION_STATUS("Activation status of loan disbursement rule cannot be empty"),
    INVALID_LOAN_DISBURSEMENT_RULE_ID("Loan disbursement rule id cannot be invalid");


    private final String message;
    LoanDisbursementRuleMessage(String message) {
        this.message = message;
    }
}
