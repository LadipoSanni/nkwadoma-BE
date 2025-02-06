package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Getter
@Setter
public class ScoreFactors {
    @JsonProperty("RegistryID")
    private String registryId;
    @JsonProperty("ScoreFactorType")
    private String scoreFactorType;
    @JsonProperty("ScoreFactorNarrative")
    private String scoreFactorNarrative;
}
