package africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle;

import lombok.Getter;

@Getter
public enum FinancierMessages {
   INVALID_FINANCIER_ID("Invalid financier id provided."),
   EMPTY_FINANCIER_PROVIDED("View all financier request cannot be empty.");

    private final String message;

    FinancierMessages(String message) {
        this.message = message;
    }

}
