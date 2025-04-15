package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.*;

@Getter
public enum InvestmentVehicleMessages {

    INVALID_INVESTMENT_VEHICLE_ID("Invalid investment vehicle Id provided."),
    INVESTMENT_VEHICLE_NOT_FOUND("Investment vehicle not found"),
    INVESTMENT_VEHICLE_NAME_EXIST("Investment vehicle name exist"),
    INVESTMENT_VEHICLE_CANNOT_BE_NULL("Investment vehicle Object cannot be empty"),
    INVESTMENT_VEHICLE_NAME_CANNOT_BE_EMPTY("Investment vehicle name cannot be empty"),
    INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL("Investment vehicle type cannot be empty"),
    INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL("Investment vehicle status cannot be empty"),
    INVESTMENT_VEHICLE_VISIBILITY_CANNOT_BE_NULL("Investment vehicle visibility cannot be empty"),
    PUBLISHED_INVESTMENT_VEHICLE_CANNOT_BE_DELETED("Published investment vehicle cannot be deleted"),
    DELETED("Deleted"),
    INVESTMENT_VEHICLE_ALREADY_PUBLISHED("Investment Vehicle already published and cannot be edited"),
    CANNOT_INVESTMENT_VEHICLE_PRIVATE_WITH_EMPTY_FINANCIER("Cannot make investment vehicle private with no financer");
    private final String message;

    InvestmentVehicleMessages(String message) {
        this.message = message;
    }
}
