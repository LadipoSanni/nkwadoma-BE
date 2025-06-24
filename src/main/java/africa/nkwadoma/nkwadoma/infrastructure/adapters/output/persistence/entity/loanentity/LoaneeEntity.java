package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
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
    @Enumerated(EnumType.STRING)
    private UploadedStatus uploadedStatus;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    private LocalDateTime referralDateTime;
    private String referredBy;
    private String reasonForDropout;
    private LocalDateTime deferredDateAndTime;
    private String deferReason;
    private boolean deferralRequested;
    private boolean deferralApproved;
    private boolean dropoutRequested;
    private boolean dropoutApproved;
}
