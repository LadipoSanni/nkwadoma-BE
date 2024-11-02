package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface EmployeeAdminEntityRepository extends JpaRepository<OrganizationEmployeeEntity,String> {
    OrganizationEmployeeEntity findByMeedlUserId(String userId);
    void deleteByMeedlUserId(String id);
    Optional<OrganizationEmployeeEntity> findByMeedlUser_CreatedBy(String createdBy);

    OrganizationEmployeeEntity findByMiddlUserId(String userId);
    List<OrganizationEmployeeEntity> findAllByOrganization(String organizationId);
}
