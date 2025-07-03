package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;

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
    BigDecimal getTuitionAmount();
    BigDecimal getTotalCohortFee();
    BigDecimal getAmountRequested();
    BigDecimal getAmountOutstanding();
    BigDecimal getAmountReceived();
    BigDecimal getTotalAmountRepaid();
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
