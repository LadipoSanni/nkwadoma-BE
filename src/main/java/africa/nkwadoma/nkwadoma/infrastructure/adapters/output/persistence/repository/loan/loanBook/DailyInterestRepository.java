package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DailyInterestEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyInterestRepository extends JpaRepository<DailyInterestEntity,String> {
}
