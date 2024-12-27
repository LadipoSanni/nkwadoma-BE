package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

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
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize ;
    private BigDecimal totalAmountAvailable;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private BigDecimal totalAmountEarned ;
    private BigDecimal totalAmountDisbursed ;
    private BigDecimal totalAmountRepaid ;
    private String mandate;
    private String costOfFund;
    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductID;
    private int numberOfLoanees;
}
