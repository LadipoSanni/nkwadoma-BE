package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements CreateLoanProductUseCase, ViewLoanProductUseCase, LoaneeUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoanProductMapper loanProductMapper;
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
        LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
        if (foundLoanProduct.getTotalNumberOfLoanees() > BigInteger.ZERO.intValue()){
            throw new LoanException("Loan product " + foundLoanProduct.getName() + " cannot be updated as it has already been loaned out");
        }
        foundLoanProduct = loanProductMapper.updateLoanProduct(foundLoanProduct,loanProduct);
        foundLoanProduct.setUpdatedAt(LocalDateTime.now());
        log.info("Loan product updated {}",  foundLoanProduct);

        return loanProductOutputPort.save(foundLoanProduct);
    }

    @Override
    public LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductId);
        return loanProductOutputPort.findById(loanProductId);
    }

    @Override
    public Loanee saveAdditionalDetails(Loanee loanee) {
        return null;
    }
}
