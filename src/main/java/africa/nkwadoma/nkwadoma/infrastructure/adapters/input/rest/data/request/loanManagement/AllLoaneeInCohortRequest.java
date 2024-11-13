package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AllLoaneeInCohortRequest {

    private String cohortId;
    private int pageSize;
    private int pageNumber;
}
