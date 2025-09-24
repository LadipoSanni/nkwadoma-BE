package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class CreditRegistryFindDetailResponse {
    @JsonProperty("SearchResult")
    private List<CustomerDetail> searchResult;
    @JsonProperty("Success")
    private boolean success;
    @JsonProperty("Errors")
    private List<String> errors;
    @JsonProperty("InfoMessage")
    private String infoMessage;
    @JsonProperty("TransactionID")
    private String transactionID;
}
