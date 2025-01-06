package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface OrganizationServiceOfferingRepository extends JpaRepository<OrganizationServiceOfferingEntity, String> {
    List<OrganizationServiceOfferingEntity> findAllByOrganizationId(String id);
}