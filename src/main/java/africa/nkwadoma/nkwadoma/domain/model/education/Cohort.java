package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
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
    @Size( max = 2500, message = "cohort description must no go beyond 2500" )
    private String cohortDescription;
    private String name;
    private ActivationStatus activationStatus;
    private CohortStatus cohortStatus;
    private LocalDateTime createdAt;
    private BigDecimal tuitionAmount ;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
    private LoanDetail loanDetail;
    private Integer numberOfLoanees = 0;
//    private int pageSize;
//    private int pageNumber;

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validateObjectName(name);
        MeedlValidator.validateUUID(createdBy);
        MeedlValidator.validateObjectInstance(startDate);
    }

    public void updateValidation() throws MeedlException {
        MeedlValidator.validateUUID(id);
        MeedlValidator.validateUUID(updatedBy);
    }
}