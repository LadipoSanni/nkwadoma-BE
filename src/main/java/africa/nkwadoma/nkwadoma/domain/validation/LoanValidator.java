package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages.*;

@Slf4j
public class LoanValidator extends MeedlValidator {
    private static final int ZERO = 0;
    private static final int MIN_MORATORIUM = 1;
    private static final int MAX_MORATORIUM = 24;
    private static final int MAX_TENOR_IN_YEARS = 10;
    private static final int MAX_TENOR_IN_MONTHS = 120;
    public static void validateLoanProductDetails(LoanProduct loanProduct) throws MeedlException {
        validateLoanProduct(loanProduct);
        validateDataElement(loanProduct.getName());
        log.info("Validating loan product {}", loanProduct.getName());
        validateDataElement(loanProduct.getMandate());
        validateDataElement(loanProduct.getTermsAndCondition());
        validateTenor(loanProduct);
        validateMoratorium(loanProduct);
        if (loanProduct.getSponsors() == null
                ||loanProduct.getSponsors().isEmpty()
                ||loanProduct.getLoanProductSize() == null
                ||loanProduct.getLoanProductSize().compareTo(BigDecimal.ZERO) <= ZERO
                ||loanProduct.getObligorLoanLimit() == null
                ||loanProduct.getObligorLoanLimit().compareTo(BigDecimal.ZERO) < ZERO
                ||loanProduct.getInterestRate() < ZERO
                ||loanProduct.getMinRepaymentAmount() == null
                ||loanProduct.getMinRepaymentAmount().compareTo(BigDecimal.ZERO) < ZERO
        ) {
            throwException(INVALID_LOAN_PRODUCT_REQUEST_DETAILS);
        }
        validateObligorAgainstProductSize(loanProduct);
    }
    private static void validateMoratorium(LoanProduct loanProduct) throws LoanException {
        if (loanProduct.getMoratorium() < MIN_MORATORIUM) throwException(MORATORIUM_BELOW_BOUND);
        if (loanProduct.getMoratorium() > MAX_MORATORIUM) throwException(MORATORIUM_ABOVE_BOUND);
    }
    private static void validateTenor(LoanProduct loanProduct) throws LoanException {
        if (loanProduct.getTenor() <= ZERO) throwException(TENOR_IS_REQUIRED);

        if (loanProduct.getTenorStatus() == null||
                StringUtils.isEmpty(loanProduct.getTenorStatus().toString())) throwException(TENOR_STATUS_REQUIRED);
        if (!(loanProduct.getTenorStatus().equals(TenorStatus.Months) ||
                loanProduct.getTenorStatus().equals(TenorStatus.Years))) throwException(INVALID_STATUS);

        if (loanProduct.getTenorStatus().equals(TenorStatus.Months))
            if (loanProduct.getTenor() > MAX_TENOR_IN_MONTHS) throwException(TENOR_STATUS_MONTH_BOND);

        if (loanProduct.getTenorStatus().equals(TenorStatus.Years))
            if (loanProduct.getTenor() > MAX_TENOR_IN_YEARS) throwException(TENOR_STATUS_YEAR_BOND);

    }
    public static void validateLoanProduct(LoanProduct loanProduct)throws MeedlException {
        if (loanProduct == null) throwException(INVALID_REQUEST);
    }
    public static void validateObligorAgainstProductSize(LoanProduct loanProduct) throws MeedlException {
        if (loanProduct.getObligorLoanLimit().compareTo(loanProduct.getLoanProductSize()) > 0) {
            throwException(OBLIGOR_LIMIT_GREATER_THAN_PRODUCT_SIZE);
        }
    }
    private static void throwException(LoanMessages message) throws LoanException {
        log.error("{} {}",LoanException.class.getName() ,message.getMessage());
        throw new LoanException(message.getMessage());
    }
}
