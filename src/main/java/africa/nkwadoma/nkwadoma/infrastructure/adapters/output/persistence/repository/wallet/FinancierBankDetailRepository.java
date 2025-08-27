package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.FinancierBankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FinancierBankDetailRepository extends JpaRepository<FinancierBankDetailEntity, String> {
    @Query("""
        SELECT fbd
        FROM FinancierBankDetailEntity fbd
        WHERE fbd.financierEntity.id = :financierId
          AND fbd.bankDetailEntity.activationStatus = 'APPROVED'
        """)
    FinancierBankDetailEntity findApprovedBankDetailByFinancierId(String financierId);

    List<FinancierBankDetailEntity> findAllByFinancierEntity_Id(String financierId);
}
