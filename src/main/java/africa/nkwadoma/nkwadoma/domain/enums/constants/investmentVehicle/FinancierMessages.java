package africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle;

import lombok.Getter;

@Getter
public class FinancierMessages {
    public static final String INVALID_FINANCIER_ID = "invalid financier id provided";

    private final String message;

    FinancierMessages(String message) {
        this.message = message;
    }

}
