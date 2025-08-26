package africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle;

import lombok.Getter;

@Getter
public enum FinancierMessages {
   INVALID_FINANCIER_ID("Invalid financier id provided."),
    NOT_A_FINANCIER("Apparently, you are not a financier. Contact admin."),
   FINANCIER_DESIGNATION_REQUIRED("Investment vehicle designation is required."),
   EMPTY_FINANCIER_PROVIDED("Financier object cannot be empty."),
   FINANCIER_INVITE_TO_VEHICLE("financier-to-vehicle-invite"),
   FINANCIER_INVITE_TO_PLATFORM("financier-to-platform-invite"),
   FINANCIER_INVITE_TO_PLATFORM_TITLE("Financier invited to Meedl platform"),
   INVALID_FINANCIER_TYPE("Please specify if financier is individual or cooperate."),
    AMOUNT_TO_INVEST_REQUIRED("Amount to invest is require."),
    COOPERATE_FINANCIER_CANNOT_BE_EMPTY("Cooperate financier cannot be empty."),;

    private final String message;

    FinancierMessages(String message) {
        this.message = message;
    }

}
