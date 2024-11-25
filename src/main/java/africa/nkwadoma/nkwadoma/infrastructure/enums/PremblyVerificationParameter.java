package africa.nkwadoma.nkwadoma.infrastructure.enums;

import lombok.Getter;

@Getter
public enum PremblyVerificationParameter {
        NIN_URL("/vnin"),
        APP_ID("app-id"),
        API_KEY("x-api-key"),
        ACCEPT("accept"),
        BVN_NUMBER("number"),
        NIN_NUMBER("number_nin"),
        APPLICATION_JSON("application/json");

       private final String value;
       PremblyVerificationParameter(String value) {
            this.value = value;
       }

}



