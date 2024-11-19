package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cohort {
    private String id;
    private String programId;
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
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    private List<LoanBreakdown> loanBreakdowns = new ArrayList<>();
    private LoanDetail loanDetail;
    private Integer numberOfLoanees = 0;
//    private int pageSize;
//    private int pageNumber;

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validateDataElement(name);
        MeedlValidator.validateUUID(createdBy);
    }

    public void updateValidation() throws MeedlException {
        MeedlValidator.validateUUID(id);
        MeedlValidator.validateUUID(updatedBy);
    }
}