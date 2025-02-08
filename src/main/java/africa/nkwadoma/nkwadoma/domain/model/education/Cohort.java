package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cohort {
    private String id;
    private String programId;
    private String organizationId;
    @Size( max = 2500, message = "cohort description must not go beyond 2500" )
    private String cohortDescription;
    private String name;
    private ActivationStatus activationStatus;
    private CohortStatus cohortStatus;
    private LocalDateTime createdAt;
    private BigDecimal tuitionAmount;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
    private LoanDetail loanDetail;
    private int numberOfLoanees = 0;
    private int numberOfReferredLoanee = 0;
    private String programName;
    private int numberOfEmployed;
    private int numberOfDropOut;



    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateObjectName(name,"Name cannot be empty");
        MeedlValidator.validateUUID(createdBy, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        MeedlValidator.validateObjectInstance(startDate,"Start date cannot be empty");
        MeedlValidator.validateNegativeAmount(tuitionAmount,"Tuition");
    }

    public void updateValidation() throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateUUID(updatedBy, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
    }

    public void validateLoanBreakDowns() throws MeedlException {
        MeedlValidator.validateLoanBreakdowns(loanBreakdowns);
    }
}