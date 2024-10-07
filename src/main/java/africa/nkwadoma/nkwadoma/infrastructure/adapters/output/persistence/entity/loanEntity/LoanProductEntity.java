package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;


import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_product")
public class LoanProductEntity {
    @Id
    @UuidGenerator
    private String id;

    @Column(unique = true)
    private String name;
    private int moratorium;
    @Enumerated(EnumType.STRING)
    private TenorStatus tenorStatus;
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
//    private InvestmentVehicle investmentVehicle;
    @Column(nullable = false, columnDefinition = "int DEFAULT 0")
    private int numberOfLoanees;
}
