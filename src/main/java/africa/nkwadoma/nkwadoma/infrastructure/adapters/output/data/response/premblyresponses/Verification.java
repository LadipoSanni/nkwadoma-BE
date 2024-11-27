package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@Slf4j
public  class Verification {

    @JsonProperty("status")
    private String status;
    private boolean validIdentity;
    @JsonProperty("reference")
    private String reference;

    public void updateValidIdentity() {
        if (StringUtils.equalsIgnoreCase("VERIFIED", this.status)) {
            validIdentity = Boolean.TRUE;
        }
    }
}