package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements CreateLoanProductUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        validateLoanProductDetails(loanProduct);
        if (loanProductOutputPort.existsByName(loanProduct.getName())) throw new LoanException("Loan product " + loanProduct.getName() + " already exists");
        log.info("Loan product {} created successfully", loanProduct.getName());
        return loanProductOutputPort.save(loanProduct);
    }

    @Override
    public void deleteLoanProductById(LoanProduct loanProduct) throws MeedlException {
        validateLoanProduct(loanProduct);
        loanProductOutputPort.deleteById(loanProduct.getId());
    }

}
