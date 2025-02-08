package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CreateCohortRequest {

    @NotBlank(message = "Program id is required")
    private String programId;
    @Size(max = 2500, message = "cohort description must no go beyond 2500")
    private String cohortDescription;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    @NotBlank(message = "Cohort name is required")
    @Size( max = 200, message = "Cohort name must not exceed 200 characters")
    private String name;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    private String imageUrl;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
}
