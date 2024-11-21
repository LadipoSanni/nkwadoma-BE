package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferEntitiy {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String loanRequestId;
    private String referredBy;
    private BigDecimal loanAmountRequested;
    private LocalDateTime dateTimeApproved;
    @Enumerated(EnumType.STRING)
    private LoanRequestStatus loanRequestStatus;
    @Enumerated(EnumType.STRING)
    private LoanOfferStatus loanOfferStatus;
    @ManyToOne
    private LoaneeEntity loanee;
    private LocalDateTime dateTimeOffered;
}
