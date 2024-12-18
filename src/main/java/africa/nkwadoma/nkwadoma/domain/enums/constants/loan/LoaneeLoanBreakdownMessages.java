package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoaneeLoanBreakdownMessages {


    AMOUNT_CANNOT_BE_LESS_THAN_ZERO("Amount Cannot Be Less Than Zero");

    private final String message;
    LoaneeLoanBreakdownMessages(String message) {
        this.message = message;
    }
}
