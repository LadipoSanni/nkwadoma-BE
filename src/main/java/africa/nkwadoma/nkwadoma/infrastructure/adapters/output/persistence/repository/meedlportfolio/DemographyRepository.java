package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlportfolio;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio.DemographyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemographyRepository extends JpaRepository<DemographyEntity, String> {
    DemographyEntity findByName(String meedl);
}
