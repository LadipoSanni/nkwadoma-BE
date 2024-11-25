package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class EditCohortLoanDetailRequest {

    @NotBlank(message = "Cohort Id is required")
    private String id;
    private String cohortDescription;
    private String name;
    private LocalDate startDate;
    private BigDecimal tuitionAmount;
}
