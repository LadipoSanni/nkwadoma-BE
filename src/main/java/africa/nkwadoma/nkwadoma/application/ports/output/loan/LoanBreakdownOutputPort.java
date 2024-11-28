package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;

import java.util.List;

public interface LoanBreakdownOutputPort {
    List<LoanBreakdown> findAllByCohortId(String id);


    List<LoanBreakdown> saveAll(List<LoanBreakdown> loanBreakdown, LoaneeLoanDetail loaneeLoanDetail);

    void deleteAll(List<LoanBreakdown> loanBreakdownList);

    List<LoanBreakdown> saveAllLoanBreakDown(List<LoanBreakdown> loanBreakdown);

    List<LoanBreakdown> finAllByLoaneeLoanDetailsId(String id) throws MeedlException;
}
