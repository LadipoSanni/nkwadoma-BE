package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanUseCase {
    Loan viewLoanDetails( String loanId) throws MeedlException;

    Loan startLoan(Loan loan) throws MeedlException;

    Page<Loan> viewAllLoans(Loan loan) throws MeedlException;

    LoanDetailSummary viewLoanTotal(String actorId,String loaneeId) throws MeedlException;

    Page<Loan> searchDisbursedLoan(Loan loan) throws MeedlException;

    Page<LoanReferral> viewAllLoanReferrals(LoanReferral request) throws MeedlException;

    Page<LoanReferral> searchLoanReferrals(LoanReferral request) throws MeedlException;
}
