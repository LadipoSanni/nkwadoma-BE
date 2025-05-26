package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DeferProgramRequest;
import org.springframework.data.domain.Page;

import java.util.*;

public interface LoaneeOutputPort {
    Loanee save(Loanee loanee) throws MeedlException;

    void deleteLoanee(String loaneeId) throws MeedlException;

    Loanee findByLoaneeEmail(String email) throws MeedlException;

    List<Loanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException;

    Optional<Loanee> findByUserId(String userId) throws MeedlException;

    Loanee findLoaneeById(String loaneeId) throws MeedlException;

    Page<Loanee> findAllLoaneeByCohortId(String cohortId , int pageSize , int pageNumber, String sortBy) throws MeedlException;

    List<Loanee> findAllLoaneesByCohortId(String id) throws MeedlException;

    List<Loanee> searchForLoaneeInCohort(String name,String cohortId) throws MeedlException;


    Page<Loanee> findAllLoaneeThatBenefitedFromLoanProduct(String id,int pageSize , int pageNumber) throws MeedlException;


    Page<Loanee> searchLoaneeThatBenefitedFromLoanProduct(String id,String name, int pageSize, int pageNumber) throws MeedlException;

    boolean checkIfLoaneeCohortExistInOrganization(String loaneeId, String organization) throws MeedlException;

    void archiveOrUnArchiveByIds(List<String> loaneesId, LoaneeStatus loaneeStatus) throws MeedlException;

}
