package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;

public interface ProgramLoanDetailOutputPort {

    ProgramLoanDetail save(ProgramLoanDetail programLoanDetail) throws MeedlException;

    ProgramLoanDetail findByProgramId(String id) throws MeedlException;

    void delete(String loanDetailsId) throws MeedlException;

    void deleteByProgramId(String id) throws MeedlException;
}
