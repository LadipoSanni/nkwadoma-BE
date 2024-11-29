package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CreditScoreResponse {
    @JsonProperty("SMARTScores")
    private List<SMARTScore> smartScores;

    @JsonProperty("ScoreFactors")
    private List<String> scoreFactors;

    @JsonProperty("Success")
    private boolean success;

    @JsonProperty("Errors")
    private List<String> errors;

    @JsonProperty("InfoMessage")
    private String infoMessage;

    @JsonProperty("TransactionID")
    private String transactionID;

    @Getter
    @Setter
    @ToString
    public static class SMARTScore {

        @JsonProperty("RegistryID")
        private String registryID;

        @JsonProperty("GenericScore")
        private int genericScore;
    }
}
