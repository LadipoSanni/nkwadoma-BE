package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CohortLoanDetailRequest {

    private String id;
    private LoanDetailRequest loanDetail;
    private String cohort;
}
