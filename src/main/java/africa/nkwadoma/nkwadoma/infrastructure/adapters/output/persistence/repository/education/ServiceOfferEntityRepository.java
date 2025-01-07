package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface ServiceOfferEntityRepository extends JpaRepository<ServiceOfferingEntity, String> {

    @Query("select soe " +
            "from ServiceOfferingEntity soe " +
            "join OrganizationServiceOfferingEntity ose on ose.serviceOfferingEntity.id = soe.id " +
            "where ose.organizationId = :organizationId")
    List<ServiceOfferingEntity> findAllByOrganizationId(@Param("organizationId") String organizationId);
}
