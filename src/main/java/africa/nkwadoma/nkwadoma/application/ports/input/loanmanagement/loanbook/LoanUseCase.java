package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;

public interface LoanUseCase {
    Loan viewLoanDetails( String loanId) throws MeedlException;

    Loan startLoan(Loan loan) throws MeedlException;

    Page<Loan> viewAllLoans(Loan loan) throws MeedlException;

    LoanDetailSummary viewTotalAmount(String actorId) throws MeedlException;
}
