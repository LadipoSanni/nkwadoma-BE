package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import org.springframework.data.domain.Page;

public interface LoaneeUsecase {

    Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException;

    Page<Loanee> viewAllLoaneeInCohort(String programId,int pageSize ,int pageNumber) throws MeedlException;
}
