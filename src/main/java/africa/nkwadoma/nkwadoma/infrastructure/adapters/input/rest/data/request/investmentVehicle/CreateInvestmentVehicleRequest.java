package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
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
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = ErrorMessages.NAME_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN)
    @Size( max = 200, message = ErrorMessages.INVESTMENT_VEHICLE_NAME_MUST_NOT_EXCEED_200_CHARACTERS)
    private String name;
    private InvestmentVehicleType investmentVehicleType;
    @Size( max = 2500, message = ErrorMessages.INVESTMENT_VEHICLE_MANDATE_MUST_NOT_EXCEED_2500_CHARACTERS)
    private String mandate;
    @Max(value = 999, message = ErrorMessages.TENURE_CANNOT_EXCEED_THREE_DIGITS)
    private BigInteger tenure;
    private BigDecimal size;
    private Float rate;
    private String trustee;
    private String custodian;
    private String bankPartner;
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = ErrorMessages.FUND_MANAGER_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN)
    private String fundManager;
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = ErrorMessages.SPONSOR_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN)
    private String sponsors;
    private BigDecimal minimumInvestmentAmount;
    private InvestmentVehicleStatus investmentVehicleStatus;
}
