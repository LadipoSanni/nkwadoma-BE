package africa.nkwadoma.nkwadoma.domain.model.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentVehicle {

    private String id;
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    @Size( max = 2500, message = "Investment vehicle mandate must not exceed 2500 characters")
    private String mandate;
    private int tenure;
    private BigDecimal size;
    private Float interestRateOffered;
    private FundRaisingStatus fundRaisingStatus;
    private Financier leads;
    private Financier contributors;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private String fundManager;
    private BigDecimal totalAvailableAmount;
    private BigDecimal minimumInvestmentAmount;
    private LocalDateTime createdDate;
    private LocalDate startDate;
    private InvestmentVehicleStatus investmentVehicleStatus;
    private String investmentVehicleLink;
    private LocalDateTime lastUpdatedDate;
    private BigDecimal netAssetValue;
    private double netAssetValueInPercent;
    private double percentageOfPortfolio;
    private LocalDateTime maturityDate;
    private VehicleClosure vehicleClosureStatus;
    private BigDecimal incomeEarned;
    private IncomeInterval incomeInterval;
    private VehicleOperation vehicleOperation;
    private InvestmentVehicleVisibility investmentVehicleVisibility;
    private Set<InvestmentVehicleDesignation> designations;
    private BigDecimal amountInvested;
    private int talentFunded;
    private BigDecimal amountFinancierInvested;
    private LocalDate dateInvested;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;


    public void validate() throws MeedlException {
        log.info("Validating a published or publishable investment vehicle {} name : {}", id, name);
        MeedlValidator.validateObjectName(name,"Investment vehicle name cannot be empty","Investment vehicle");
        MeedlValidator.validateObjectInstance(startDate,"Start date cannot be empty");
        MeedlValidator.validateObjectName(trustee,"Trustee cannot be empty","Trustee");
        MeedlValidator.validateObjectName(custodian,"Custodian cannot be empty","Custodian");
        MeedlValidator.validateBankPartnerName(bankPartner,"Bank Partner cannot be empty","Bank Partner");
        MeedlValidator.validateObjectName(fundManager,"Fund Manager cannot be empty","Fund Manager");
        MeedlValidator.validateIntegerDataElement(tenure,"Tenure cannot be less than 1");
        validateTenure(tenure);
        MeedlValidator.validateDataElement(investmentVehicleType.name(), "Investment vehicle type is required");
        if (investmentVehicleType.equals(InvestmentVehicleType.COMMERCIAL)) {
            MeedlValidator.validateFloatDataElement(interestRateOffered, "Investment Vehicle Rate Cannot be empty or less than zero");
        }else {
            MeedlValidator.validateRate(interestRateOffered,"Investment Vehicle Rate Cannot be empty");
        }
        MeedlValidator.validateDataElement(mandate,"Mandate cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(size, "Investment vehicle size is required");
        MeedlValidator.validateNegativeAmount(minimumInvestmentAmount,"Minimum investment ");
        log.info("Validation completed for investment vehicle.");
    }

    public void setValues() {
        setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);
        setCreatedDate(LocalDateTime.now());
        setInvestmentVehicleStatus(InvestmentVehicleStatus.PUBLISHED);
        if (this.totalAvailableAmount == null){
            log.info("Setting up total available amount for {} investment vehicle.", name);
            setTotalAvailableAmount(new BigDecimal("0.00"));
        }
        if (!investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PRIVATE) &&
                ! investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PUBLIC)) {
            setInvestmentVehicleVisibility(InvestmentVehicleVisibility.DEFAULT);
        }
    }

    public void validateTenure(int tenure) throws MeedlException {
        boolean patternMatches = Pattern.matches("^-?\\d{1,3}$", String.valueOf(tenure));
        if (!patternMatches) {
            throw new MeedlException("Tenure must not be greater than 3 digits");
        }
    }

    public void validateDraft() throws MeedlException {
        log.info("Validating investment vehicle for draft {}, name :{} ", id, name);
        MeedlValidator.validateObjectName(name,"Investment vehicle name cannot be empty","Investment vehicle");
        MeedlValidator.validateIntegerDataElement(tenure,"Tenure cannot be less than 1");
    }

    public void validateVehicleStatuses() throws MeedlException {
        int nonNullCount = 0;
        if (vehicleOperation.getCouponDistributionStatus() != null) nonNullCount++;
        if (vehicleOperation.getFundRaisingStatus() != null) nonNullCount++;
        if (vehicleOperation.getDeployingStatus() != null) nonNullCount++;
        if (vehicleClosureStatus.getRecollectionStatus() != null) nonNullCount++;
        if (vehicleClosureStatus.getMaturity() != null) nonNullCount++;
        if (nonNullCount > 1){
            throw new MeedlException("More than one status cannot be set on investment vehicle");
        }else if (nonNullCount < 1){
            throw new MeedlException("Investment vehicle must have one status");
        }
    }
}
