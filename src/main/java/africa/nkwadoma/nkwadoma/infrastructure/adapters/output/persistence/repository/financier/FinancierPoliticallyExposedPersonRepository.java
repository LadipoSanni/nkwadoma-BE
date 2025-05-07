package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierPoliticallyExposedPersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancierPoliticallyExposedPersonRepository extends JpaRepository<FinancierPoliticallyExposedPersonEntity,String> {
    List<FinancierPoliticallyExposedPersonEntity> findAllByFinancier_Id(String financierId);
}
