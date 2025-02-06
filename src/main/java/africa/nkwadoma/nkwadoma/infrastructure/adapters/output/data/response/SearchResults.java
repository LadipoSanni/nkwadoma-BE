package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Getter
@Setter
public class SearchResults {
    @JsonProperty("Relevance")
    private int relevance;
    @JsonProperty("RegistryID")
    private String registryId;
    @JsonProperty("Name")
    private String name;
}
