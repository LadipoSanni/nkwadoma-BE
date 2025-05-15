package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

import java.util.List;

public interface LoanProductOutputPort {
    LoanProduct save(LoanProduct loanProduct) throws MeedlException;

    void deleteById(String id) throws MeedlException;

    boolean existsByName(String name) throws MeedlException;

    LoanProduct findById(String id) throws MeedlException;

    LoanProduct findByName(String name) throws MeedlException;

    Page<LoanProduct> findAllLoanProduct(LoanProduct loanProduct);

    List<LoanProduct> search(String loanProductName) throws MeedlException;
}
