package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistoryEntity,String> {
    @Query("SELECT r FROM RepaymentHistoryEntity r WHERE :loaneeId IS NULL OR r.loanee.id = :loaneeId")
    Page<RepaymentHistoryEntity> findRepaymentHistoryByLoaneeIdOrAll(@Param("loaneeId") String loaneeId, Pageable pageable);}
