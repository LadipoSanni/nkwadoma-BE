package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

public interface OrganizationEntityRepository extends JpaRepository<OrganizationEntity,String> {
    Optional<OrganizationEntity> findByEmail(String email);

    Optional<OrganizationEntity> findByRcNumber(String rcNumber);

    List<OrganizationEntity> findByNameContainingIgnoreCase(String trim);

    Optional<OrganizationEntity> findByTaxIdentity(String tin);

    Optional<OrganizationEntity> findByName(String name);

    @Query("""
      select o.id as organizationId,
      o.name as name,
      o.logoImage as logoImage,
      lm.loanRequestCount as loanRequestCount,
      lm.loanDisbursalCount as loanDisbursalCount,
      lm.loanOfferCount as loanOfferCount,
      lm.loanReferralCount as loanReferralCount
      from OrganizationEntity o
      join LoanMetricsEntity lm on lm.organizationId = o.id
    """)
    List<OrganizationProjection> findAllWithLoanMetrics();

}
