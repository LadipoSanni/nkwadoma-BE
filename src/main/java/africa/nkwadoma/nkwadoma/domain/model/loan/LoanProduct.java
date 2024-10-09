package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanProductStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class LoanProduct {
    private String id;
    private String name;
    private int moratorium;
    private LoanProductStatus loanProductStatus;
    private TenorStatus tenorStatus;
    private int tenor;
    private double interestRate;
    @Size(max=2500)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize;
    private BigDecimal amountAvailable;
    private LocalDateTime createdAtDate;
    private LocalDateTime updatedAtDate;
    private BigDecimal amountEarned ;
    private BigDecimal amountDisbursed;
    private BigDecimal amountRepaid ;

    @Size(max=2500)
    private String mandate;

    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductId;
    private int numberOfLoanees;


}
