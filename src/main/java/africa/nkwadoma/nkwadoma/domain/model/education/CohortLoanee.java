package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CohortLoanee {

    private String id;
    private Loanee loanee;
    private String cohort;

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(cohort);
        MeedlValidator.validateObjectInstance(loanee);
    }
}
