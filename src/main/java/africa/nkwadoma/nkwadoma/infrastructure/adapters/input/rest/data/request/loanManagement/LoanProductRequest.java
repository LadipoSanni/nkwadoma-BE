package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlPatterns;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.SponsorsRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.LoanRestValidationMessages.*;

@Getter
@Setter
public class LoanProductRequest {
    private String id;
    private String investmentVehicleId;
    private List<SponsorsRequest> sponsors;
    private BigDecimal totalAmountAvailable;
    private BigDecimal totalAmountEarned;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;

//    private ActivationStatus loanProductStatus;
    private String bankPartner;
    private String disbursementTerms;
    @PositiveOrZero(message = "Cost of fund must be zero or positive")
    private double costOfFund;
    @NotBlank(message = LOAN_PRODUCT_NAME_REQUIRED)
    @Pattern(regexp = MeedlPatterns.CHARACTER_REGEX, message = ErrorMessages.NAME_MUST_NOT_START_OR_END_WITH_APOSTROPHE_OR_HYPHEN)
    @Size( max = 200, message = "Loan product name must not exceed 200 characters")
    private String name;

    @NotBlank(message = LOAN_PRODUCT_MANDATE_REQUIRED)
    private String mandate;

    private BigDecimal loanProductSize;
    private BigDecimal obligorLoanLimit;

    @PositiveOrZero(message = "Interest rate must be zero or positive")
    private double interestRate;
    @Positive(message = "Moratorium must be a positive number")
    @Max(value = 999, message = "Moratorium cannot exceed three digits.")
    private BigInteger moratorium;
    @Positive(message = "Tenor must be a positive number")
    @Max(value = 999, message = "Tenor cannot exceed three digits.")
    private BigInteger tenor;
    private BigDecimal minRepaymentAmount;

    @NotBlank(message = LOAN_PRODUCT_TERMS_AND_CONDITIONS_REQUIRED)
    private String termsAndCondition;
    private List<Vendor> vendors;
}
