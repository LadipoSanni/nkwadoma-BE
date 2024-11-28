package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreditRegistryLoginResponse {
    @JsonProperty("SessionCode")
    private String SessionCode;
    @JsonProperty("EmailAddress")
    private String EmailAddress;
    @JsonProperty("AgentID")
    private String AgentID;
    @JsonProperty("AgentName")
    private String AgentName;
    @JsonProperty("SubscriberID")
    private String SubscriberID;
    @JsonProperty("SubscriberName")
    private String SubscriberName;
    @JsonProperty("Success")
    private boolean Success;
    @JsonProperty("Errors")
    private List<String> Errors;
    @JsonProperty("InfoMessage")
    private String InfoMessage;
    @JsonProperty("TransactionID")
    private String TransactionID;

}
