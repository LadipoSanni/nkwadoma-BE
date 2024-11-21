package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CreateCohortRequest {


    private String programId;
    @Size( max = 2500, message = "cohort description must no go beyond 2500" )
    private String cohortDescription;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private String name;
    private LocalDate startDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
    private LoanDetailRequest LoanDetail;
}
