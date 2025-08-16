package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CooperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CooperationRepository extends JpaRepository<CooperationEntity, String> {
    CooperationEntity findByName(String email);

    boolean existsByName(String name);

    CooperationEntity findByEmail(String email);
}
