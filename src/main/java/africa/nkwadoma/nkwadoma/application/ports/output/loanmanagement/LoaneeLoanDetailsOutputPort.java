package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanSummaryProjection;

import java.time.Month;
import java.util.List;

public interface LoaneeLoanDetailsOutputPort {
    LoaneeLoanDetail save(LoaneeLoanDetail loaneeLoanDetail);

    void delete(String id) throws MeedlException;

    LoaneeLoanDetail findByCohortLoaneeId(String cohortLoaneeId) throws MeedlException;

    LoanDetailSummary getLoaneeLoanSummary(String userId) throws MeedlException;

    LoaneeLoanDetail findByCohortAndLoaneeId(String cohortId, String loaneeId) throws MeedlException;

    LoaneeLoanDetail findByLoanRequestId(String id) throws MeedlException;

    List<LoaneeLoanDetail> findAllByNotNullAmountOutStanding();

    List<LoaneeLoanDetail> findAllWithDailyInterestByMonthAndYear(Month month, int year);

    LoanDetailSummary getOrganizationLoanSummary(String organization) throws MeedlException;
}
