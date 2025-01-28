package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;


@Setter
@Getter
public class CreateInvestmentVehicleRequest {
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = "Name must not start or end with apostrophe or hyphen")
    @Size( max = 200, message = "Investment vehicle name must not exceed 200 characters")
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    @Size( max = 2500, message = "Investment vehicle mandate must not exceed 2500 characters")
    private String mandate;
    @Max(value = 999, message = "Tenure cannot exceed three digits.")
    private BigInteger tenure;
    private BigDecimal size;
    private Float rate;
    private String trustee;
    private String custodian;
    private String bankPartner;
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = "Fund manager must not start or end with apostrophe or hyphen")
    private String fundManager;
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = "Sponsor must not start or end with apostrophe or hyphen")
    private String sponsors;
    private BigDecimal minimumInvestmentAmount;
}
