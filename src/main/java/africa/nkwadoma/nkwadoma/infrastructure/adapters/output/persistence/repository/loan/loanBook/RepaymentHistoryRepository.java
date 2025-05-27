package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistoryEntity,String> {
}
