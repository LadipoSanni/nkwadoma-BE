package africa.nkwadoma.nkwadoma.infrastructure.enums;

    public enum PremblyParameter {
        NIN_URL("/vnin"),
        APP_ID("app-id"),
        API_KEY("x-api-key"),
        ACCEPT("accept"),
        BVN_NUMBER("number"),
        NIN_NUMBER("number_nin"),
        APPLICATION_JSON("application/json");

       private final String value;
       PremblyParameter(String value) {
            this.value = value;
       }

        public String getValue() {
            return value;
        }
    }



