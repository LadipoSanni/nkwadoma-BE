package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message;

public class ErrorMessages {
    public static final String INVALID_RC_NUMBER = "Registration number must start with 'RC' followed by exactly 7 digits.";
    public static final String INVALID_TIN = "Tax identity number must contain 9 - 15 characters and can only have a hyphen special character.";
    public static final String INVALID_INPUT_PROVIDED = "Invalid input provided";
    public static final String NAME_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN = "Name must not start or end with apostrophe or hyphen";
    public static final String INVESTMENT_VEHICLE_NAME_MUST_NOT_EXCEED_200_CHARACTERS = "Investment vehicle name must not exceed 200 characters";
    public static final String INVESTMENT_VEHICLE_MANDATE_MUST_NOT_EXCEED_2500_CHARACTERS = "Investment vehicle mandate must not exceed 2500 characters";
    public static final String TENURE_CANNOT_EXCEED_THREE_DIGITS = "Tenure cannot exceed three digits.";
    public static final String SPONSOR_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN = "Sponsor must not start or end with apostrophe or hyphen";
    public static final String FUND_MANAGER_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN = "Fund manager must not start or end with apostrophe or hyphen";
    public static final String INVALID_SORT_PARAMETER = "Invalid sort parameter provided";
}
