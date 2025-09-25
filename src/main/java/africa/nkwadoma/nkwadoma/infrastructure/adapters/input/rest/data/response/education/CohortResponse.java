package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBreakdownResponse;
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
    private CohortType cohortType;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private BigDecimal totalCohortFee = BigDecimal.ZERO;
    private BigDecimal amountRequested;
    private BigDecimal amountOutstanding;
    private BigDecimal amountReceived;
    private BigDecimal totalAmountRepaid;
    private BigDecimal averageStartingSalary;
    private double employmentRate;
    private double repaymentRate;
    private double debtPercentage;
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
    private String programName;
    private int stillInTraining ;
    private int numberOfDropout ;
    private int numberEmployed ;
    private int numberOfPendingLoanOffers;
    private int numberOfLoanRequest = 0;
}
