package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.LoaneeBankDetailEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoaneeBankDetailRepository extends JpaRepository<LoaneeBankDetailEntity, String> {
    List<LoaneeBankDetailEntity> findAllByLoaneeEntity_id(String loaneeId);
    @Query("""
        SELECT lbd
        FROM LoaneeBankDetailEntity lbd
        WHERE lbd.loaneeEntity.id = :loaneeEntityId
          AND lbd.bankDetailEntity.activationStatus = 'APPROVED'
        """)
    LoaneeBankDetailEntity findApprovedBankDetailByLoaneeId(String loaneeEntityId);
}
