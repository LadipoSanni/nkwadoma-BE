package africa.nkwadoma.nkwadoma.infrastructure.commons;

public enum PremblyVerificationMessage {
    SERVICE_UNAVAILABLE("This Service is Unavailable. Please try again in a few minutes."),
    NIN_NOT_FOUND("This NIN cannot be found. Please provide a correct NIN."),
    VERIFICATION_SUCCESSFUL("Verification Successful"),
    NIN_VERIFIED("Verified"),
    VERIFICATION_UNSUCCESSFUL("Verification Unsuccessful");

    private final String message;
    PremblyVerificationMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
