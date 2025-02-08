package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class CreditScoreResponse {
    @JsonProperty("SMARTScores")
    private List<SMARTScore> smartScores;

    @JsonProperty("ScoreFactors")
    private List<ScoreFactors> scoreFactors;

    @JsonProperty("Success")
    private boolean success;

    @JsonProperty("Errors")
    private List<String> errors;

    @JsonProperty("InfoMessage")
    private String infoMessage;

    @JsonProperty("TransactionID")
    private String transactionID;

    @JsonProperty("SearchResult")
    private List<SearchResults> searchResults;

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
