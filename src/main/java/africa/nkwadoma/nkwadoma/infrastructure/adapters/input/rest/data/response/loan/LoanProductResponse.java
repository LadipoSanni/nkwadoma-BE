package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class LoanProductResponse {
    private String id;
    private String name;
    private int moratorium;
    private DurationType tenorStatus;
    private int tenor;
    private double interestRate;
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize ;
    private BigDecimal amountAvailable;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAtDate;
    private BigDecimal amountEarned ;
    private BigDecimal amountDisbursed ;
    private BigDecimal amountRepaid ;
    private String mandate;
    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductID;
    private int numberOfLoanees;
}
