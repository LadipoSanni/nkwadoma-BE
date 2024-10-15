package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanProductEntityRepository extends JpaRepository<LoanProductEntity,String> {
    boolean existsByName(String name);

    Optional<LoanProductEntity> findByName(String name);
}
