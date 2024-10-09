package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductEntityRepository extends JpaRepository<LoanProductEntity,String> {
    boolean existsByName(String name);
}
