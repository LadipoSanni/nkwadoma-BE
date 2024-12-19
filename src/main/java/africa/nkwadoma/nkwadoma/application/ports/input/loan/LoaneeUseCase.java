package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoaneeUseCase {

    Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException;

    Loanee viewLoaneeDetails(String loaneeId) throws MeedlException;

    Page<Loanee> viewAllLoaneeInCohort(String cohortId,int pageSize ,int pageNumber) throws MeedlException;
    LoanReferral referLoanee(String loaneeId) throws MeedlException;

    List<Loanee> searchForLoaneeInCohort(String name,String cohortId) throws MeedlException;

}
