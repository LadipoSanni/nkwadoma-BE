package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CreditScoreRequest {
    @JsonProperty("SessionCode")
    private String sessionCode;

    @JsonProperty("CustomerRegistryIDList")
    private List<String> customerRegistryIDList;

    @JsonProperty("EnquiryReason")
    private String enquiryReason;
}
