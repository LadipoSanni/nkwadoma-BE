package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CohortProjection {
    String getId();
    String getName();
    String getProgramId();
    String getOrganizationId();
    String getCohortDescription();
    String getProgramName();
    ActivationStatus getActivationStatus();
    CohortStatus getCohortStatus();
    CohortType getCohortType();
    BigDecimal getTuitionAmount();
    BigDecimal getTotalCohortFee();
    BigDecimal getAmountRequested();
    BigDecimal getAmountOutstanding();
    BigDecimal getAmountReceived();
    BigDecimal getAmountRepaid();
    LocalDate getStartDate();
    LocalDate getExpectedEndDate();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
    String getUpdatedBy();
    String getImageUrl();
    Integer getNumberOfLoanees();
    Integer getNumberOfReferredLoanee();
    Integer getStillInTraining();
    Integer getNumberOfDropout();
    Integer getNumberEmployed();
    Integer getNumberOfPendingLoanOffers();
    Integer getNumberOfLoanRequest();
}
