package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.prembly;

import lombok.Getter;

@Getter
public enum PremblyResponseCode {
    SUCCESSFUL("00"),
    SUCCESSFUL_RECORD_NOT_FOUND("01"),
    SERVICE_NOT_AVAILABLE("02"),
    INSUFFICIENT_WALLET_BALANCE("03"),
    BLOCKED("07");

    private final String code;
    PremblyResponseCode(String code){
        this.code = code;
    }
}
