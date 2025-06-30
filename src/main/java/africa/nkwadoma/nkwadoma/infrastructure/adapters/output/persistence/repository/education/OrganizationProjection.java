package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import java.math.BigDecimal;

public interface OrganizationProjection {
    String getOrganizationId();
    String getName();
    int getLoanRequestCount();
    int getLoanDisbursalCount();
    int getLoanReferralCount();
    int getLoanOfferCount();
    String getLogoImage();
    int getNumberOfLoanees();
    int getNumberOfCohort();
    int getNumberOfPrograms();
    BigDecimal getTotalDebtRepaid();
    BigDecimal getTotalCurrentDebt();
    BigDecimal getTotalHistoricalDebt();
    BigDecimal getTotalAmountReceived ();
    BigDecimal getTotalAmountRequested ();
}
