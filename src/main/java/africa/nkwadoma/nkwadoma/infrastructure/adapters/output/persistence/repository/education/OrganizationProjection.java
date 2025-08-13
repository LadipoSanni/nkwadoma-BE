package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OrganizationProjection {
    String getOrganizationId();
    String getName();
    Integer getLoanRequestCount();
    Integer getLoanDisbursalCount();
    Integer getLoanReferralCount();
    Integer getLoanOfferCount();
    String getLogoImage();
    String getEmail();
    String getWebsiteAddress();
    String getInvitedDate();
    Integer getNumberOfLoanees();
    Integer getNumberOfCohort();
    Integer getNumberOfPrograms();
    BigDecimal getTotalDebtRepaid();
    BigDecimal getTotalCurrentDebt();
    BigDecimal getTotalHistoricalDebt();
    BigDecimal getTotalAmountReceived ();
    BigDecimal getTotalAmountRequested ();
    ActivationStatus getActivationStatus();
    Double getDebtPercentage();
    Double getRepaymentRate();
}
