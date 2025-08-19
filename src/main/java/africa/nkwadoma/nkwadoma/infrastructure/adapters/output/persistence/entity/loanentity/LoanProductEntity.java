package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;


import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private ActivationStatus loanProductStatus;
    private int tenor;
    private double interestRate;
    private double costOfFund;
    @Size(max=15000)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize = BigDecimal.ZERO;
    private BigDecimal totalAmountAvailable = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmountEarned = BigDecimal.ZERO;
    private BigDecimal totalAmountDisbursed = BigDecimal.ZERO;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;

    @Size(max=5500)
    private String mandate;
    private String sponsor;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String investmentVehicleId;
    private String investmentVehicleName;
    @Column(nullable = false, columnDefinition = "int DEFAULT 0")
    private int totalNumberOfLoanee;
    private int totalNumberOfLoanProduct;
}
