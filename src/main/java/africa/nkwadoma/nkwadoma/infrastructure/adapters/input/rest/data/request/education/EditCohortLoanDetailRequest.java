package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class EditCohortLoanDetailRequest {

    private String id;
    private String programId;
    private String cohortDescription;
    private String createdBy;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    private LoanDetailRequest LoanDetail;
}
