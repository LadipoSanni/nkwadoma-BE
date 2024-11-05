package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.ViewLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements CreateLoanProductUseCase, ViewLoanProductUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        loanProduct.validateLoanProductDetails();
        UserIdentity foundUser = userIdentityOutputPort.findById(loanProduct.getCreatedBy());
        identityManagerOutPutPort.verifyUserExistsAndIsEnabled(foundUser);
        log.info("Loan product {} created successfully", loanProduct.getName());
        return loanProductOutputPort.save(loanProduct);
    }
    @Override
    public void deleteLoanProductById(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateDataElement(loanProduct.getId());
        loanProductOutputPort.deleteById(loanProduct.getId());
    }
    @Override
    public Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct) {
        return loanProductOutputPort.findAllLoanProduct(loanProduct);
    }

    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateUUID(loanProduct.getId());
        return loanProductOutputPort.updateLoanProduct(loanProduct);
    }

    @Override
    public LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductId);
        return loanProductOutputPort.findById(loanProductId);
    }
}
