package africa.nkwadoma.nkwadoma.infrastructure.enums;

import lombok.*;

@Getter
public enum IdentityVerificationParameter {
    NIN_URL("/vnin"),
    APP_ID("app-id"),
    API_KEY("x-api-key"),
    ACCEPT("accept"),
    BVN_NUMBER("number"),
    NIN_NUMBER("number_nin"),
    APPLICATION_JSON("application/json");

    private final String value;

    IdentityVerificationParameter(String value) {
        this.value = value;
    }

}



