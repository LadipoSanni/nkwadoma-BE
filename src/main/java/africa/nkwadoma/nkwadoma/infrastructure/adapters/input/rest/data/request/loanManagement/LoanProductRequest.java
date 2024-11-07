package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.LoanRestValidationMessages.*;

@Getter
@Setter
public class LoanProductRequest {
    private String id;
    private String fundProductId;
    private BigDecimal totalAmountAvailable;
    private BigDecimal totalAmountEarned;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;

    private ActivationStatus loanProductStatus;
    private String bankPartner;
    private String disbursementTerms;
    @PositiveOrZero(message = "Interest rate must be zero or positive")
    private double costOfFund;
    @NotBlank(message = LOAN_PRODUCT_NAME_REQUIRED)
    private String name;

    @NotBlank(message = LOAN_PRODUCT_MANDATE_REQUIRED)
    private String mandate;

    private List<String> sponsors;
    private BigDecimal loanProductSize;
    private BigDecimal obligorLoanLimit;

    @PositiveOrZero(message = "Interest rate must be zero or positive")
    private double interestRate;
    private int moratorium;
    private int tenor;
    private BigDecimal minRepaymentAmount;

    @NotBlank(message = LOAN_PRODUCT_TERMS_AND_CONDITIONS_REQUIRED)
    private String termsAndCondition;
    private List<Vendor> vendors;
}
