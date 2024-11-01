package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationEntityRepository extends JpaRepository<OrganizationEntity,String> {
    Optional<OrganizationEntity> findByEmail(String email);

    List<OrganizationEntity> findAllByName(String name);
}
