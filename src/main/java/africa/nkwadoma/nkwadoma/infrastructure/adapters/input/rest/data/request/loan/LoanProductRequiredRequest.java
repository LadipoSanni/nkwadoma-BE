package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.LoanRestValidationMessages.*;

@Getter
@Setter
public class LoanProductRequiredRequest extends LoanProductRequest {
    @NotBlank(message = LOAN_PRODUCT_NAME_REQUIRED)
    private String name;

    @Size(max=2500)
    @NotBlank(message = LOAN_PRODUCT_MANDATE_REQUIRED)
    private String mandate;

    @NotNull(message = "Sponsors list cannot be null")
    @Size(min = 1, message = "Sponsors list cannot be empty")
    private List<String> sponsors;

    @NotNull(message = "Loan product size is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Loan product size must be greater than zero")
    private BigDecimal loanProductSize;

    @NotNull(message = "Obligor loan limit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Obligor loan limit must be greater than zero")
    private BigDecimal obligorLoanLimit;

    @PositiveOrZero(message = "Interest rate must be zero or positive")
    private double interestRate;

    @PositiveOrZero(message = "Moratorium must be zero or positive")
    private int moratorium;

    @PositiveOrZero(message = "Tenor must be zero or positive")
    private int tenor;

    @NotNull(message = "Minimum repayment amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum repayment amount must be greater than zero")
    private BigDecimal minRepaymentAmount;

    @Size(max=2500)
    @NotBlank(message = LOAN_PRODUCT_TERMS_AND_CONDITIONS_REQUIRED)
    private String termsAndCondition;

}
