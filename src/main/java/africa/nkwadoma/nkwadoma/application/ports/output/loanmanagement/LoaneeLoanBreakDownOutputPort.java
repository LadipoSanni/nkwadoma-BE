package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;

import java.util.List;

public interface LoaneeLoanBreakDownOutputPort {

    List<LoaneeLoanBreakdown> saveAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns, CohortLoanee cohortLoanee) throws MeedlException;

    void deleteAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns) throws MeedlException;

    List<LoaneeLoanBreakdown> findAllLoaneeLoanBreakDownByCohortLoaneeId(String cohortLoaneeId) throws MeedlException;

    LoaneeLoanBreakdown findById(String loaneeLoanBreakdownId) throws MeedlException;

    void deleteByCohortLoaneeid(String id);
}
