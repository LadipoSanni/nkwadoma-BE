package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.math.*;

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
    private String mandate;
    private int tenure;
    private BigDecimal size;
    private Float rate;
    private FundRaisingStatus fundRaisingStatus;
    private InvestmentVehicleFinancier leads;
    private InvestmentVehicleFinancier contributors;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(name);
        MeedlValidator.validateIntegerDataElement(tenure);
        MeedlValidator.validateDataElement(investmentVehicleType.name(), "Investment vehicle type is required");
        MeedlValidator.validateFloatDataElement(rate);
        MeedlValidator.validateBigDecimalDataElement(size);
    }
}
