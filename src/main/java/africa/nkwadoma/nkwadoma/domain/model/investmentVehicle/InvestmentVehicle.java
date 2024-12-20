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
    private String sponsor;
    private BigDecimal minimumInvestmentAmount;
    private LocalDate startDate;



    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(name);
        MeedlValidator.validateObjectName(trustee);
        MeedlValidator.validateObjectName(custodian);
        MeedlValidator.validateObjectName(bankPartner);
        MeedlValidator.validateObjectName(fundManager);
        MeedlValidator.validateObjectName(sponsor);
        MeedlValidator.validateIntegerDataElement(tenure);
        MeedlValidator.validateDataElement(investmentVehicleType.name(), "Investment vehicle type is required");
        MeedlValidator.validateFloatDataElement(rate);
        MeedlValidator.validateBigDecimalDataElement(size);
        MeedlValidator.validateBigDecimalDataElement(minimumInvestmentAmount);
    }
}
