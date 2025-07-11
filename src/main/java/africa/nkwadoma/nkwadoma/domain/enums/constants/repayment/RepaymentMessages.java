package africa.nkwadoma.nkwadoma.domain.enums.constants.repayment;

import lombok.Getter;

@Getter
public enum RepaymentMessages {

    REPAYMENT_CANNOT_BE_NULL("RepaymentHistory cannot be empty"),
    INVALID_REPAYMENT_ID_PROVIDED("Repayment Id cannot be empty");

    private final String message;

    RepaymentMessages(String message) {
        this.message = message;
    }
}
