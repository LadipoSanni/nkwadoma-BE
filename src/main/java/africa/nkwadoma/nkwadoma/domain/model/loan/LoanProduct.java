package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Slf4j
@Getter
@Setter
@ToString
public class LoanProduct {
    private String id;
    private String createdBy;
    private String name;
    private int moratorium;
    private ActivationStatus loanProductStatus;
    private int tenor;
    private double interestRate;
    private double costOfFund;
    @Size(max = 15000)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize;
    private BigDecimal totalAmountAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmountEarned;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;

    @Size(max = 5500)
    private String mandate;

    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductId;
    private int totalNumberOfLoanees;
    private int totalNumberOfLoanProduct;
    private List<Vendor> vendors;

    private int pageSize;
    private int pageNumber;

    public void validateLoanProductDetails() throws MeedlException {
        MeedlValidator.validateDataElement(name);
        MeedlValidator.validateDataElement(termsAndCondition);
        MeedlValidator.validateDataElement(mandate);
        validateLoanProductSize();
        validateObligorLimit();
    }

    private void validateObligorLimit() throws MeedlException {
        MeedlValidator.validateObjectInstance(obligorLoanLimit);
        if (obligorLoanLimit.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()) {
            throw new LoanException(LoanMessages.INVALID_OBLIGOR_LIMIT.getMessage());
        }
    }

    private void validateLoanProductSize() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProductSize);
        if (loanProductSize.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()) {
            throw new LoanException(LoanMessages.INVALID_LOAN_PRODUCT_SIZE.getMessage());
        }
    }

    public void setTenor(int tenor) {
        if (tenor < BigInteger.ZERO.intValue()) {
            this.tenor = BigInteger.ZERO.intValue();
        } else {
            this.tenor = tenor;
        }
    }

    public void setMoratorium(int moratorium) {
        if (moratorium < BigInteger.ZERO.intValue()) {
            this.moratorium = BigInteger.ZERO.intValue();
        } else {
            this.moratorium = moratorium;
        }
    }

    public void setMinRepaymentAmount(BigDecimal minRepaymentAmount) {
        if (minRepaymentAmount != null) {
            if (minRepaymentAmount.compareTo(BigDecimal.ZERO) < BigDecimal.ZERO.intValue()) {
                this.minRepaymentAmount = minRepaymentAmount;
            } else {
                this.minRepaymentAmount = minRepaymentAmount;
            }
        }
    }

    public void setInterestRate(double interestRate) {
        if (interestRate < BigDecimal.ZERO.intValue()) {
            this.interestRate = BigDecimal.ZERO.intValue();
        } else {
            this.interestRate = interestRate;
        }
    }
}
