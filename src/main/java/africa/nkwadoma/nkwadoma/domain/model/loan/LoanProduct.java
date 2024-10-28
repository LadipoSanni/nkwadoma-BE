package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class LoanProduct {
    private String id;
    private String actorId;
    private String name;
    private int moratorium;
    private ActivationStatus loanProductStatus;
    private int tenor;
    private double interestRate;
    private double costOfFund;
    @Size(max=15000)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize;
    private BigDecimal totalAmountAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmountEarned;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;

    @Size(max=5500)
    private String mandate;

    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductId;
    private int totalNumberOfLoanees;

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
        if (obligorLoanLimit.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
            throw new LoanException(LoanMessages.INVALID_OBLIGOR_LIMIT.getMessage());
        }
    }

    private void validateLoanProductSize() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProductSize);
        if (loanProductSize.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
            throw new LoanException(LoanMessages.INVALID_LOAN_PRODUCT_SIZE.getMessage());
        }
    }

    public void setTenor(int tenor){
        if (tenor < BigInteger.ZERO.intValue()) {
            this.tenor = BigInteger.ZERO.intValue();
        }else {
            this.tenor = tenor;
        }
    }
    public void setMoratorium(int moratorium) {
        if (moratorium < BigInteger.ZERO.intValue()){
            this.moratorium = BigInteger.ZERO.intValue();
        }else {
            this.moratorium = moratorium;
        }
    }
    public void setMinRepaymentAmount(BigDecimal minRepaymentAmount){
        if (minRepaymentAmount != null) {
            if (minRepaymentAmount.compareTo(BigDecimal.ZERO) < BigDecimal.ZERO.intValue()){
                this.minRepaymentAmount = minRepaymentAmount;
            }else {
                this.minRepaymentAmount = minRepaymentAmount;
            }
        }
    }
    public void setInterestRate(double interestRate){
        if (interestRate < BigDecimal.ZERO.intValue()) {
            this.interestRate = BigDecimal.ZERO.intValue();
        }else {
            this.interestRate = interestRate;
        }
    }

}
