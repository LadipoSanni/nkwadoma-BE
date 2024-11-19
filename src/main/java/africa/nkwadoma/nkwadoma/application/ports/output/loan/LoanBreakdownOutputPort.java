package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.model.education.*;

import java.util.*;

public interface LoanBreakdownOutputPort {
    List<LoanBreakdown> findAllByCohortId(String id);


    List<LoanBreakdown> saveAll(List<LoanBreakdown> loanBreakdown, LoaneeLoanDetail loaneeLoanDetail);

    void deleteAll(List<LoanBreakdown> loanBreakdownList);

    List<LoanBreakdown> saveAllLoanBreakDown(List<LoanBreakdown> loanBreakdown);
}
