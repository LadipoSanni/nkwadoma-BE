package africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement;

import lombok.Getter;

@Getter
public enum DisbursementRuleMessages {
      EMPTY_DISBURSEMENT_RULE("Disbursement rule cannot be empty"),
    INVALID_DISBURSEMENT_RULE_NAME("Disbursement rule name cannot be empty"),
      DISBURSEMENT_RULE_NOT_FOUND("Disbursement rule not found"),
      INVALID_DISBURSEMENT_RULE_ACTIVATION_STATUS("Activation status of disbursement rule cannot be empty"),
      INVALID_DISBURSEMENT_RULE_ID("Disbursement rule id cannot be invalid");

    private final String message;
    DisbursementRuleMessages(String message) {
        this.message = message;
    }

}
