package africa.nkwadoma.nkwadoma.domain.model.education;


import africa.nkwadoma.nkwadoma.domain.enums.EmploymentStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanDetailEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
public class CohortLoanee {

    private String id;
    private Cohort cohort;
    private Loanee loanee;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoaneeStatus loaneeStatus;
    private OnboardingMode onboardingMode;
    private UploadedStatus uploadedStatus;
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
    private double interestRate;
    private double debtPercentage;
    private double repaymentPercentage;
    private String highestLevelOfEducation;
    private String programName;
    private String organizationName;
    private String interestIncurred;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(createdBy, CohortMessages.CREATED_BY_CANNOT_BE_EMPTY.getMessage());
    }
}
