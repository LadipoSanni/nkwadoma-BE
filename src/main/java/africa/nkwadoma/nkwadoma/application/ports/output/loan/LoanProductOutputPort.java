package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;

public interface LoanProductOutputPort {
    LoanProduct save(LoanProduct loanProduct);

    void deleteById(String id) throws MiddlException;

    boolean existsByName(String name) throws MiddlException;

    LoanProduct findById(String id) throws MiddlException;

    LoanProduct findByName(String name) throws LoanException;
}
