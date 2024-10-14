package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import org.springframework.data.jpa.repository.*;

public interface ServiceOfferEntityRepository extends JpaRepository<ServiceOfferingEntity, String> {

}
