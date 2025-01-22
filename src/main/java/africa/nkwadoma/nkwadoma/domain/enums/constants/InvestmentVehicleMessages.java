package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.*;

@Getter
public enum InvestmentVehicleMessages {


    INVESTMENT_IDENTITY_CANNOT_BE_NULL("Investment vehicle id cannot be null"),
    INVESTMENT_VEHICLE_NOT_FOUND("Investment vehicle not found"),
    INVESTMENT_VEHICLE_NAME_EXIST("Investment vehicle name exist"),
    INVESTMENT_VEHICLE_CANNOT_BE_NULL("Investment vehicle Object cannot be null"),
    INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY("Investment vehicle name cannot be empty");
    private final String message;

    InvestmentVehicleMessages(String message) {
        this.message = message;
    }
}
