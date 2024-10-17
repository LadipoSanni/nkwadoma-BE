package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeAdminEntityRepository extends JpaRepository<OrganizationEmployeeEntity,String> {
    OrganizationEmployeeEntity findByMiddlUserId(String userId);
}
