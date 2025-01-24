package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.james.mime4j.dom.datetime.DateTime;

import java.math.*;
import java.time.LocalDate;

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
    private InvestmentVehicleFinancier leads;
    private InvestmentVehicleFinancier contributors;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private String fundManager;
    private String sponsors;
    private BigDecimal minimumInvestmentAmount;
    private LocalDate startDate;



    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(name,"Name cannot be empty");
        MeedlValidator.validateObjectName(trustee,"Trustee cannot be empty");
        MeedlValidator.validateObjectName(custodian,"Custodian cannot be empty");
        MeedlValidator.validateObjectName(bankPartner,"Bank Partner cannot be empty");
        MeedlValidator.validateObjectName(fundManager,"Fund Manager cannot be empty");
        MeedlValidator.validateObjectName(sponsors,"Sponsor cannot be empty");
        MeedlValidator.validateIntegerDataElement(tenure,"Tenure cannot be less that 1");
        MeedlValidator.validateDataElement(investmentVehicleType.name(), "Investment vehicle type is required");
        MeedlValidator.validateFloatDataElement(rate,"Investment Vehicle Rate Cannot be empty or less than zero");
        MeedlValidator.validateDataElement(mandate,"Mandate cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(size);
        MeedlValidator.validateNegativeAmount(minimumInvestmentAmount,"Minimum investment");
    }

    public void setValues() {
        setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);
        setStartDate(LocalDate.now());
    }
}
