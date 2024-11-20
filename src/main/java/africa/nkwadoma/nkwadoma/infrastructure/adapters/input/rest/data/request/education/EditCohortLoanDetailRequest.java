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
public class EditCohortLoanDetailRequest {

    private String id;
    private String cohortDescription;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    private BigDecimal tuitionAmount ;
}
