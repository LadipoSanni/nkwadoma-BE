package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class LoanProduct {
    private String id;
    private String name;
    private int moratorium;
    private ActivationStatus loanProductStatus;
    private TenorStatus tenorStatus;
    private int tenor;
    private double interestRate;
    private double costOfFund;
    @Size(max=2500)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize;
    private BigDecimal amountAvailable;
    private LocalDateTime createdAtDate;
    private LocalDateTime updatedAtDate;
    private BigDecimal amountEarned ;
    private BigDecimal amountDisbursed;
    private BigDecimal amountRepaid ;

    @Size(max=2500)
    private String mandate;

    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductId;
    private int numberOfLoanees;

    public void validateLoanProductDetails() throws MeedlException {
        MeedlValidator.validateDataElement(getTermsAndCondition());
        validateTenor();
        validateMoratorium();
        MeedlValidator.validateDataElement(mandate);
        if (interestRate < BigDecimal.ZERO.intValue()) {
            throw new LoanException(LoanMessages.INVALID_LOAN_PRODUCT_REQUEST_DETAILS.getMessage());
        }
        validateLoanProductSize();
        validateObligorLimit();
        validateMinRepaymentAmount();
    }

    private void validateMinRepaymentAmount() throws LoanException {
        if (obligorLoanLimit != null) {
            if (obligorLoanLimit.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
                throw new LoanException(LoanMessages.MINIMUM_REPAYMENT_AMOUNT_REQUIRED.getMessage());
            }
        }
    }

    private void validateObligorLimit() throws LoanException {
        if (obligorLoanLimit != null) {
            if (obligorLoanLimit.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
                throw new LoanException(LoanMessages.INVALID_OBLIGOR_LIMIT.getMessage());
            }
        }
    }

    private void validateLoanProductSize() throws LoanException {
        if (loanProductSize != null){
            if (loanProductSize.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
                throw new LoanException(LoanMessages.INVALID_LOAN_PRODUCT_SIZE.getMessage());
            }
        }
    }

    private void validateTenor() throws LoanException {
        int MAX_TENOR_IN_YEARS = 10;
        int MAX_TENOR_IN_MONTHS = 120;
        if (tenor <= BigDecimal.ZERO.intValue()) {
            throw new LoanException(LoanMessages.TENOR_IS_REQUIRED.getMessage());
        }
        if (ObjectUtils.isEmpty(tenorStatus) ||
                StringUtils.isEmpty(tenorStatus.toString())){
            throw new LoanException(LoanMessages.TENOR_STATUS_REQUIRED.getMessage());
        }
        if (tenorStatus.equals(TenorStatus.Months))
            if (tenor > MAX_TENOR_IN_MONTHS){
                throw new LoanException(LoanMessages.TENOR_STATUS_MONTH_BOND.getMessage());
            }

        if (tenorStatus.equals(TenorStatus.Years))
            if (tenor > MAX_TENOR_IN_YEARS){
                throw new LoanException(LoanMessages.TENOR_STATUS_YEAR_BOND.getMessage());
            }

    }
    private void validateMoratorium() throws LoanException {
        int MIN_MORATORIUM = 1;
        int MAX_MORATORIUM = 24;
        if (moratorium < MIN_MORATORIUM){
            throw new LoanException(LoanMessages.MORATORIUM_BELOW_BOUND.getMessage()
            );
        }
        if (moratorium > MAX_MORATORIUM) {
            throw new LoanException(LoanMessages.MORATORIUM_ABOVE_BOUND.getMessage());
        }
    }

}
