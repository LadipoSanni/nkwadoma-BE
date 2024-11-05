package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CreateCohortRequest {

    private String programId;
    private String cohortDescription;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private String createdBy;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
}
