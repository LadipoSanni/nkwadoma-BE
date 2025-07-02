package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationLoanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationLoanDetailRepository extends JpaRepository<OrganizationLoanDetailEntity,String> {
    OrganizationLoanDetailEntity findByOrganizationId(String id);
}
