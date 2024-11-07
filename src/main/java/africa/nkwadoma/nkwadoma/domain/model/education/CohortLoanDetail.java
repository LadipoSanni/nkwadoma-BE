package africa.nkwadoma.nkwadoma.domain.model.education;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CohortLoanDetail {

    private String id;
    private LoanDetail loanDetail;
    private String cohort;

}
