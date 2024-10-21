package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface ProgramRepository extends JpaRepository<ProgramEntity, String> {

    @Query("select p from ProgramEntity p where p.name = :programName")
    Optional<ProgramEntity> findByName(String programName);
    boolean existsByName(String programName);

    Page<ProgramEntity> findAllByOrganizationEntityId(String organizationId, Pageable pageable);
}
