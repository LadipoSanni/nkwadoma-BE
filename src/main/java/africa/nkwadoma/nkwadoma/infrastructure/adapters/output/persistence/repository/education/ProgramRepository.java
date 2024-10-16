package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface ProgramRepository extends JpaRepository<ProgramEntity, String> {

    Optional<ProgramEntity> findByName(@Param("programName") String programName);

    boolean existsByName(String programName);

    List<ProgramEntity> findAllByOrganizationEntityId(String organizationId);
}
