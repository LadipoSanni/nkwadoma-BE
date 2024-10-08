package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages.*;

@Slf4j
public class LoanValidator extends MiddleValidator {
    public static void validateLoanProductDetails(LoanProduct loanProduct) throws MiddlException {
        validateDataElement(loanProduct.getName());
        validateDataElement(loanProduct.getMandate());
        validateDataElement(loanProduct.getTermsAndCondition());
        if (loanProduct.getSponsors() == null
                ||loanProduct.getSponsors().isEmpty()
                ||loanProduct.getLoanProductSize() == null
                ||loanProduct.getLoanProductSize().compareTo(BigDecimal.ZERO) < 0
                ||loanProduct.getObligorLoanLimit() == null
                ||loanProduct.getObligorLoanLimit().compareTo(BigDecimal.ZERO) < 0
                ||loanProduct.getInterestRate() < 0
                ||loanProduct.getMoratorium() < 0
                ||loanProduct.getTenor() < 0
                ||loanProduct.getMinRepaymentAmount() == null
        ) {
            log.error(INVALID_LOAN_PRODUCT_REQUEST_DETAILS.getMessage(),loanProduct);
            throw new LoanException(INVALID_LOAN_PRODUCT_REQUEST_DETAILS.getMessage());
        }
    }

    public static void validateLoanProduct(LoanProduct loanProduct)throws MiddlException {
        if (loanProduct == null) throw new LoanException(INVALID_REQUEST.getMessage());
    }
    public static void validateObligorAndProductSize(LoanProduct loanProduct) throws MiddlException {
        if (loanProduct.getObligorLoanLimit().compareTo(loanProduct.getLoanProductSize()) > 0) {
            log.error(OBLIGOR_LIMIT_GREATER_THAN_PRODUCT_SIZE.getMessage());
            throw new LoanException(OBLIGOR_LIMIT_GREATER_THAN_PRODUCT_SIZE.getMessage());
        }
    }
}
