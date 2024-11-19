package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
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
    private String cohortDescription;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private String name;
    private LocalDate startDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
    private LoanDetailRequest LoanDetail;
}
