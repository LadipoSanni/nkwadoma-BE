package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.PoliticallyExposedPersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticallyExposedPersonRepository extends JpaRepository<PoliticallyExposedPersonEntity, String> {
}
