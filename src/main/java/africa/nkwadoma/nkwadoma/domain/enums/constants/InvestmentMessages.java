package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.*;

@Getter
public enum InvestmentMessages {


    INVESTMENT_IDENTITY_CANNOT_BE_NULL("Investment vehicle id cannot be null"),
    INVESTMENT_VEHICLE_NOT_FOUND("Investment vehicle not found"),
    INVESTMENT_VEHICLE_NAME_EXIST("Investment vehicle name exist");
    private final String message;

    InvestmentMessages(String message) {
        this.message = message;
    }
}
