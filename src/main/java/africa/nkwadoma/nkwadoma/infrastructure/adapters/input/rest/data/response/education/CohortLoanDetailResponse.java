package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.LoanDetailRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CohortLoanDetailResponse {

    private String id;
    private LoanDetailResponse loanDetail;
    private String cohort;
}
