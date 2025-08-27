package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum BankDetailMessages {
    INVALID_BANK_DETAIL("Invalid bank detail"),
    INVALID_BANK_DETAIL_ID("Invalid bank detail id provided");

    private final String message;

    BankDetailMessages(String message) {
        this.message = message;
    }
}
