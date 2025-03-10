package africa.nkwadoma.nkwadoma.infrastructure.commons;

public enum IdentityVerificationMessage {
    SERVICE_UNAVAILABLE("This Service is Unavailable. Please try again in a few minutes."),
    NIN_NOT_FOUND("This NIN cannot be found. Please provide a correct NIN."),
    VERIFICATION_SUCCESSFUL("Verification Successful"),
    NIN_VERIFIED("Verified"),
    VERIFICATION_UNSUCCESSFUL("Verification Unsuccessful"),
    PREMBLY_UNAVAILABLE("Prembly server error."),
    SMILEID_UNAVAILABLE("SmileId server error."),
    INVALID_BVN("Bvn is required {}"),
    INVALID_NIN("Nin is required"),
    INSUFFICIENT_WALLET_BALANCE("Insufficient wallet balance"),
    PROVIDE_VALID_BVN("Please provide a valid Bvn"),
    IDENTITY_VERIFICATION_CANNOT_BE_NULL("Identity verification cannot be null"),
    PROVIDE_VALID_NIN("Please provide a valid Nin");



    private final String message;

    IdentityVerificationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
