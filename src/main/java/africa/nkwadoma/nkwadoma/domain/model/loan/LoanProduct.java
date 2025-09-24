package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvalidInputException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

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
    private BigDecimal totalOutstandingLoan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmountEarned;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;

    @Size(max=5500)
    private String mandate;

    private String sponsor;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String investmentVehicleId;
    private int totalNumberOfLoanee;
    private int totalNumberOfLoanProduct;
    private String investmentVehicleName;
    private List<Vendor> vendors;
    private List<String> sponsorIds;
    private List<Financier> sponsors;

    private int pageSize;
    private int pageNumber;
    private DisbursementRule disbursementRule;

    public void validateCostOfFund() throws MeedlException{
        if (costOfFund > 100) throw new MeedlException("Cost of fund cannot be greater than 100");
    }

    public void validateLoanProductDetails() throws MeedlException {
        log.info("Started loan product validation");
        MeedlValidator.validateObjectName(name,"Loan product name cannot be empty","Loan product");
        MeedlValidator.validateDataElement(termsAndCondition, "Loan product terms and conditions required.");
        MeedlValidator.validateDataElement(mandate, "Mandate terms required.");
        MeedlValidator.validateUUID(investmentVehicleId,"Investment vehicle ID must be valid and cannot also be empty");
        validateLoanProductSize();
        validateObligorLimit();
        validateTenor();
        validateMoratorium();
        validateCostOfFund();
        validateDisbursementRule();

        log.info("Ended loan product validation successfully... ");
    }

    private void validateDisbursementRule() throws MeedlException {
        if (ObjectUtils.isNotEmpty(this.disbursementRule)){
            log.info("Validating disbursement terms at the loan product level");
            disbursementRule.validate();
        }
    }

    private void validateMoratorium() throws InvalidInputException {
        if (moratorium < BigInteger.ONE.intValue()) {
            throw new InvalidInputException("Moratorium can not be less than 1.");
        }
        if (moratorium > tenor){
            throw new InvalidInputException("Moratorium cannot be more than tenor");
        }
        if (moratorium > BigInteger.valueOf(999).intValue()) {
            throw new InvalidInputException("Moratorium can not be more than three digits.");
        }

    }

    private void validateTenor() throws InvalidInputException {
        if (tenor < BigInteger.ONE.intValue()) {
            throw new InvalidInputException("Tenor can not be less than 1.");
        }

        if (tenor > BigInteger.valueOf(999).intValue()) {
            throw new InvalidInputException("Tenor can not be more than three digits.");
        }
    }

    private void validateObligorLimit() throws MeedlException {
        MeedlValidator.validateObjectInstance(obligorLoanLimit, LoanMessages.OBLIGOR_LOAN_LIMIT_REQUIRED.getMessage());
        if (obligorLoanLimit.compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
            throw new LoanException(LoanMessages.INVALID_OBLIGOR_LIMIT.getMessage());
        }
    }

    private void validateLoanProductSize() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProductSize, LoanMessages.LOAN_PRODUCT_SIZE_REQUIRED.getMessage());
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

    public void addInvestmentVehicleValues(InvestmentVehicle investmentVehicle){
        setInvestmentVehicleName(investmentVehicle.getName());
    }
}
