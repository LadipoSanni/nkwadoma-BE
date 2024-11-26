package africa.nkwadoma.nkwadoma.infrastructure.enums.prembly;

import lombok.Getter;

@Getter
    public enum PremblyParameter {
    NIN_URL("/vnin"),
    APP_ID("app-id"),
    API_KEY("x-api-key"),
    ACCEPT("accept"),
    BVN_NUMBER("number"),
    NIN_NUMBER("number"),
    APPLICATION_JSON("application/json"),
    NIN_FACE_URL("/nin_w_face"),
    BVN_FACE("/bvn_w_face"),
    NIN_LIVENESS_URL("/biometrics/face/liveliness_check"),
    NIN_IMAGE("image"),
    BVN_IMAGE("image");

    private final String value;

    PremblyParameter(String value) {
        this.value = value;
    }


}