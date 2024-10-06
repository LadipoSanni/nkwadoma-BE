package africa.nkwadoma.nkwadoma.domain.model.loan;

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
    @Column(unique = true)
    private String name;
    private int moratorium;
//    @Enumerated(EnumType.STRING)
//    private TenureStatus tenorStatus;
    private int tenor;
    private double interestRate;
    @Size(max=2500)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize = BigDecimal.ZERO;
    private BigDecimal amountAvailable = BigDecimal.ZERO;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAtDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAtDate;
    private BigDecimal amountEarned = BigDecimal.ZERO;
    private BigDecimal amountDisbursed = BigDecimal.ZERO;
    private BigDecimal amountRepaid = BigDecimal.ZERO;

    @Size(max=2500)
    private String mandate;
    @ElementCollection
    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
//    @OneToOne
//    private FundProduct fundProduct;
    @Column(nullable = false, columnDefinition = "int DEFAULT 0")
    private int numberOfLoanees;


}
