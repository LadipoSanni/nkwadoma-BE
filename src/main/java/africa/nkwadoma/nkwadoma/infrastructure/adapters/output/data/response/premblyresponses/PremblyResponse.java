package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class PremblyResponse {
    @JsonProperty("status")
    private boolean verificationCallSuccessful;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("verification")
    private Verification verification;

    @JsonProperty("session")
    private Object session;

}
