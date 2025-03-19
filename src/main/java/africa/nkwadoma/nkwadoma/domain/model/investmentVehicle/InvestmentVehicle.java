package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

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
    private Float rate;
    private FundRaisingStatus fundRaisingStatus;
    private Financier leads;
    private Financier contributors;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private String fundManager;
    private String sponsors;
    private BigDecimal totalAvailableAmount;
    private BigDecimal minimumInvestmentAmount;
    private LocalDate createdDate;
    private LocalDate startDate;
    private InvestmentVehicleStatus investmentVehicleStatus;
    private String investmentVehicleLink;
    private LocalDate lastUpdatedDate;
    private InvestmentVehicleVisibility investmentVehicleVisibility;



    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(name,"Name cannot be empty","InvestmentVehicle");
        MeedlValidator.validateObjectName(trustee,"Trustee cannot be empty","Trustee");
        MeedlValidator.validateObjectName(custodian,"Custodian cannot be empty","Custodian");
        MeedlValidator.validateObjectName(bankPartner,"Bank Partner cannot be empty","Bank Partner");
        MeedlValidator.validateObjectName(fundManager,"Fund Manager cannot be empty","Fund Manager");
        MeedlValidator.validateObjectName(sponsors,"Sponsor cannot be empty","Sponsor");
        MeedlValidator.validateIntegerDataElement(tenure,"Tenure cannot be less than 1");
        validateTenure(tenure);
        MeedlValidator.validateDataElement(investmentVehicleType.name(), "Investment vehicle type is required");
        if (investmentVehicleType.equals(InvestmentVehicleType.COMMERCIAL)) {
            MeedlValidator.validateFloatDataElement(rate, "Investment Vehicle Rate Cannot be empty or less than zero");
        }else {
            MeedlValidator.validateRate(rate,"Investment Vehicle Rate Cannot be empty");
        }
        MeedlValidator.validateDataElement(mandate,"Mandate cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(size, "Investment vehicle size is required");
        MeedlValidator.validateNegativeAmount(minimumInvestmentAmount,"Minimum investment ");
    }

    public void setValues() {
        setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);
        setCreatedDate(LocalDate.now());
        setInvestmentVehicleStatus(InvestmentVehicleStatus.PUBLISHED);
        setTotalAvailableAmount(size);
        setInvestmentVehicleVisibility(InvestmentVehicleVisibility.DEFAULT);
    }

    public void validateTenure(int tenure) throws MeedlException {
        boolean patternMatches = Pattern.matches("^-?\\d{1,3}$", String.valueOf(tenure));
        if (!patternMatches) {
            throw new MeedlException("Tenure must not be greater than 3 digits");
        }
    }

    public void validateDraft() throws MeedlException {
        MeedlValidator.validateObjectName(name,"Name cannot be empty","InvestmentVehicle");
        MeedlValidator.validateIntegerDataElement(tenure,"Tenure cannot be less than 1");
    }
}
