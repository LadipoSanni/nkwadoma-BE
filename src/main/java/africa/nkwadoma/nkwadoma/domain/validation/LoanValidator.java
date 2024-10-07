package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanProductException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
@Slf4j
public class LoanValidator {
    public static void validateLoanProductDetails(LoanProduct loanProduct) throws MiddlException {
        if (StringUtils.isEmpty(loanProduct.getName())
                ||StringUtils.isEmpty(loanProduct.getMandate())
                ||loanProduct.getSponsors() == null
                ||loanProduct.getSponsors().isEmpty()
                ||loanProduct.getLoanProductSize() == null
                ||loanProduct.getLoanProductSize().compareTo(BigDecimal.ZERO) < 0
                ||loanProduct.getObligorLoanLimit() == null
                ||loanProduct.getObligorLoanLimit().compareTo(BigDecimal.ZERO) < 0
                ||loanProduct.getInterestRate() < 0
                ||loanProduct.getMoratorium() < 0
                ||loanProduct.getTenor() < 0
                ||loanProduct.getMinRepaymentAmount() == null
                ||StringUtils.isEmpty(loanProduct.getTermsAndCondition())
        ) {
            log.error("Invalid or empty request details to create loan product {} ",loanProduct);
            throw new LoanProductException("Invalid or empty request details to create loan product");
        }
    }

    public static void validateLoanProduct(LoanProduct loanProduct)throws MiddlException {
        if (loanProduct == null) throw new LoanProductException("Invalid details provided");
    }
}
