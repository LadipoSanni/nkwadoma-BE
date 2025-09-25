package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;


import africa.nkwadoma.nkwadoma.domain.enums.EmploymentStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanDetailEntity;
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
public class CohortLoaneeEntity {

    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private CohortEntity cohort;
    @ManyToOne
    private LoaneeEntity loanee;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne
    private LoaneeLoanDetailEntity loaneeLoanDetail;
    @Enumerated(EnumType.STRING)
    private LoaneeStatus loaneeStatus;
    @Enumerated(EnumType.STRING)
    private OnboardingMode onboardingMode;
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;
    private String trainingPerformance;
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
