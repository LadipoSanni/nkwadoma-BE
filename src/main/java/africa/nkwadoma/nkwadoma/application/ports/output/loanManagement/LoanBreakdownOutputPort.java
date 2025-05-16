package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;

import java.util.*;

public interface LoanBreakdownOutputPort {
    List<LoanBreakdown> findAllByCohortId(String id) throws MeedlException;

    void deleteAll(List<LoanBreakdown> loanBreakdownList);

    List<LoanBreakdown> saveAllLoanBreakDown(List<LoanBreakdown> loanBreakdown);


}
