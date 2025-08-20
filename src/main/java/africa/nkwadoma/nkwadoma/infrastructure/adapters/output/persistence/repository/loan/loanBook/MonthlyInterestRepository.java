package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.MonthlyInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MonthlyInterestRepository extends JpaRepository<MonthlyInterestEntity, String> {
    Optional<MonthlyInterestEntity> findByCreatedAt(LocalDateTime localDateTime);
}
