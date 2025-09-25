package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class
LoanProductResponse {
    private String id;
    private String name;
    private int moratorium;
    private int tenor;
    private double interestRate;
    private String termsAndCondition;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private BigDecimal totalAmountAvailable;
    private BigDecimal availableAmountToBeDisbursed;
    private BigDecimal availableAmountToBeOffered;
    private BigDecimal totalAmountDisbursed ;
    private BigDecimal obligorLoanLimit;
    private BigDecimal totalOutstandingLoan;
    private BigDecimal totalAmountRepaid ;
    private BigDecimal totalAmountEarned ;
    private BigDecimal loanProductSize ;
    private String costOfFund;
    private String mandate;
    private String sponsor;
    private String bankPartner;
    private String disbursementTerms;
    private String investmentVehicleId;
    private String investmentVehicleName;
    private int totalNumberOfLoanee;
    private BigDecimal minRepaymentAmount;
    private List<Vendor> vendors;
    private List<FinancierResponse> sponsors;
}
