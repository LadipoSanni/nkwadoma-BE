package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanProductEntityRepository extends JpaRepository<LoanProductEntity,String> {
    boolean existsByName(String name);

    Optional<LoanProductEntity> findByName(String name);
    Optional<LoanProductEntity> findByNameIgnoreCase(String name);

    Page<LoanProductEntity> findByNameContainingIgnoreCase(String loanProductName, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);

}
