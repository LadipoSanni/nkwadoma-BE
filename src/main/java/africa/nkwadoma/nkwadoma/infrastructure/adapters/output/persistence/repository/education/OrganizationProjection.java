package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface OrganizationProjection {
    String getOrganizationId();
    String getName();
    String getRcNumber();
    Integer getLoanRequestCount();
    Integer getLoanDisbursalCount();
    Integer getLoanReferralCount();
    Integer getLoanOfferCount();
    String getLogoImage();
    String getEmail();
    String getWebsiteAddress();
    LocalDateTime getInvitedDate();
    Integer getNumberOfLoanees();
    Integer getNumberOfCohort();
    Integer getNumberOfPrograms();
    BigDecimal getTotalDebtRepaid();
    BigDecimal getTotalCurrentDebt();
    BigDecimal getTotalHistoricalDebt();
    BigDecimal getTotalAmountReceived ();
    BigDecimal getTotalAmountRequested ();
    ActivationStatus getActivationStatus();
    String getInviterFullName();
    Double getDebtPercentage();
    Double getRepaymentRate();
}
