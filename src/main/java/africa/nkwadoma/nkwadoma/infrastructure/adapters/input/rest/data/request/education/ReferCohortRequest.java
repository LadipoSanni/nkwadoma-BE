package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReferCohortRequest {
    @NotBlank(message = "Cohort ID is required")
    private String cohortId;
    private List<String> loaneeIds;
}
