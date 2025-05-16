package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;

import java.util.List;

public interface LoaneeLoanBreakDownOutputPort {

    List<LoaneeLoanBreakdown> saveAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns, Loanee loanee) throws MeedlException;

    void deleteAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns) throws MeedlException;

    List<LoaneeLoanBreakdown> findAllLoaneeLoanBreakDownByLoaneeId(String loaneeId) throws MeedlException;
}
