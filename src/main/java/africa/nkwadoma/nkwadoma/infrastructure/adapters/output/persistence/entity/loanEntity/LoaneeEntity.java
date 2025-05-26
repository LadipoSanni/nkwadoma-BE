package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@ToString
public class LoaneeEntity {
    @Id
    @UuidGenerator
    private String id;
    private String cohortId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime creditScoreUpdatedAt;
    private int creditScore;
    private String registryId;
    @OneToOne
    private UserEntity userIdentity;
    @OneToOne
    private LoaneeLoanDetailEntity loaneeLoanDetail;
    @Enumerated(EnumType.STRING)
    private LoaneeStatus loaneeStatus;
    @Enumerated(EnumType.STRING)
    private OnboardingMode onboardingMode;
    private LocalDateTime referralDateTime;
    private String referredBy;
    private String setReasonForDropOut;
}
