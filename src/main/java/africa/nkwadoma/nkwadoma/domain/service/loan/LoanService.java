package africa.nkwadoma.nkwadoma.domain.service.loan;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.validateLoanProduct;
import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.validateLoanProductDetails;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements CreateLoanProductUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MiddlException {
        validateLoanProduct(loanProduct);
        validateLoanProductDetails(loanProduct);
//        validateObligorAndProductSize(loanProduct);;
        return loanProductOutputPort.save(loanProduct);
    }
}
