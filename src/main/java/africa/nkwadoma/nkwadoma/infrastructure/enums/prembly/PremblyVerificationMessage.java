package africa.nkwadoma.nkwadoma.infrastructure.enums.prembly;

import lombok.Getter;

@Getter
public enum PremblyVerificationMessage {

        SERVICE_UNAVAILABLE("This Service is Unavailable. Please try again in a few minutes."),
        NIN_NOT_FOUND("This NIN cannot be found. Please provide a correct NIN."),
        VERIFICATION_SUCCESSFUL("Verification successful"),
        NIN_VERIFIED("Verified"),
        VERIFICATION_UNSUCCESSFUL("Verification unsuccessful"),
        PREMBLY_UNAVAILABLE("Prembly server error."),
        INSUFFICIENT_WALLET_BALANCE("Insufficient wallet balance"),
        PREMBLY_FACE_CONFIRMATION("Liveliness check failed: Face Occluded.... kindly try better positioning"),
        PREMBY_FACE_DOES_NOT_MATCH("Face does not match"),
        PENDING("PENDING"),
        VERIFIED("VERIFIED"),
        NOT_VERIFIED("NOT_VERIFIED");





        private final String message;

        PremblyVerificationMessage(String message){
            this.message = message;
        }
}
