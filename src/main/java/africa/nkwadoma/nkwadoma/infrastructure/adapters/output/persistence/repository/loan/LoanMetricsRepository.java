package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface LoanMetricsRepository extends JpaRepository<LoanMetricsEntity, String> {
    Optional<LoanMetricsEntity> findDistinctTopByOrderByLoanRequestCountDesc();

    Optional<LoanMetricsEntity> findByOrganizationId(String organizationId);


    @Query("SELECT " +
            "(SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount)) AS totalNumberOfLoans, " +
            "CASE WHEN (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount)) > 0 " +
            "THEN (SUM(m.loanRequestCount) * 100.0 / (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount))) " +
            "ELSE 0.0 END AS loanRequestPercentage, " +
            "CASE WHEN (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount)) > 0 " +
            "THEN (SUM(m.loanDisbursalCount) * 100.0 / (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount))) " +
            "ELSE 0.0 END AS loanDisbursalPercentage, " +
            "CASE WHEN (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount)) > 0 " +
            "THEN (SUM(m.loanReferralCount) * 100.0 / (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount))) " +
            "ELSE 0.0 END AS loanReferralPercentage, " +
            "CASE WHEN (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount)) > 0 " +
            "THEN (SUM(m.loanOfferCount) * 100.0 / (SUM(m.loanRequestCount) + SUM(m.loanDisbursalCount) + SUM(m.loanReferralCount) + SUM(m.loanOfferCount))) " +
            "ELSE 0.0 END AS loanOfferPercentage " +
            "FROM LoanMetricsEntity m")
    LoanMetricsProjection calculateLoanMetrics();
}
