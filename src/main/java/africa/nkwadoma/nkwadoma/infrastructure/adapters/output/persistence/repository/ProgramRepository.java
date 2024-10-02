package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramRepository extends JpaRepository<ProgramEntity, String> {
    Optional<ProgramEntity> findByName(String programName);
}
