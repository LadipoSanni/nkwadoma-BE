package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.TrainingInstituteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingInstituteRepository extends JpaRepository<TrainingInstituteEntity, String> {
}