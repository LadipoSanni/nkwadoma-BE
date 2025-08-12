package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class LoaneeResponse {
    private String id;
    private String cohortId;
    private String loanId;
    private String createdBy;
    private int creditScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserIdentityResponse userIdentity;
    private LoaneeLoanDetailResponse loaneeLoanDetail;
    private OnboardingMode onboardingMode;
    private UploadedStatus uploadedStatus;
    private LoaneeStatus loaneeStatus;
    private String highestLevelOfEducation;
    private String nameOfPreviousInstitution;
    private String programOfStudy;
    private String institutionName;
    private String programName;
    private String cohortName;
    private LocalDate cohortStartDate;
    private String fitnessToWorkRating;
    private BigDecimal loanAmount;
    private Double interestRate;
    private int paymentMoratoriumPeriod;
    private String termsAndConditions;
    private int tenor;
    private ActivationStatus activationStatus;
}
