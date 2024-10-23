package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ServiceOfferEntityRepository extends JpaRepository<ServiceOfferingEntity, String> {

    List<ServiceOfferingEntity> findBy(String id);
}
