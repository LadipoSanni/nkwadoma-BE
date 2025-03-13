package africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle;

import lombok.Getter;

@Getter
public enum FinancierMessages {
   INVALID_FINANCIER_ID("Invalid financier id provided."),
   EMPTY_FINANCIER_PROVIDED("Financier object cannot be empty."),
   FINANCIER_INVITE_TO_VEHICLE("financier-to-vehicle-invite");

    private final String message;

    FinancierMessages(String message) {
        this.message = message;
    }

}
