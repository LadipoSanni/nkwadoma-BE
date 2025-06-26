package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoanBookResponse {
    private Cohort cohort;
    private List<CohortLoanee> loanees;
}
