package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class EditCohortRequest {

    @NotBlank(message = "Cohort Id is required")
    private String id;
    @Size(max = 2500, message = "cohort description must not go beyond 2500")
    private String cohortDescription;
    private String name;
    private LocalDate startDate;
    private BigDecimal tuitionAmount;
}
