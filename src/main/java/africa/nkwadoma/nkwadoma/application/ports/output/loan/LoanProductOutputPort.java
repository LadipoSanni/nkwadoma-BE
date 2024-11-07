package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import org.springframework.data.domain.Page;

public interface LoanProductOutputPort {
    LoanProduct save(LoanProduct loanProduct) throws MeedlException;

    void deleteById(String id) throws MeedlException;

    boolean existsByName(String name) throws MeedlException;

    LoanProduct findById(String id) throws MeedlException;

    LoanProduct findByName(String name) throws MeedlException;

    Page<LoanProduct> findAllLoanProduct(LoanProduct loanProduct);
}
