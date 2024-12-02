package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanBreakdownResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CohortResponse {

    private String id;
    private String programId;
    private String organizationId;
    private String cohortDescription;
    private String name;
    private ActivationStatus activationStatus;
    private CohortStatus cohortStatus;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private List<LoanBreakdownResponse> loanBreakdowns = new ArrayList<>();
    private LoanDetailResponse loanDetail;
    private int numberOfLoanees = 0;
    private int numberOfReferredLoanee = 0;
}
