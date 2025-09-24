package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.commons.IdentityVerificationMessage;
import lombok.Getter;

@Getter
public enum DisbursementRuleMessages {
      EMPTY_DISBURSEMENT_RULE("Disbursement rule cannot be empty"),
      DISBURSEMENT_RULE_NOT_FOUND("Disbursement rule not found"),
      INVALID_DISBURSEMENT_RULE("Disbursement rule id cannot be invalid");

    private final String message;
    DisbursementRuleMessages(String message) {
        this.message = message;
    }

}
