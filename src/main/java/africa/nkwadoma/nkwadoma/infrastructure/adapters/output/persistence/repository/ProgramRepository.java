package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import jakarta.ws.rs.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.Optional;

public interface ProgramRepository extends JpaRepository<ProgramEntity, String> {

    @Query("select p from ProgramEntity p where p.name = :programName")
    Optional<ProgramEntity> findByName(@Param("programName") String programName);
}
