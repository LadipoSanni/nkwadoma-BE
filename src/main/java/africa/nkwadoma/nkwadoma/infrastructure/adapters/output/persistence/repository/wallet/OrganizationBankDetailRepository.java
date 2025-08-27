package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.OrganizationBankDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganizationBankDetailRepository extends JpaRepository<OrganizationBankDetailEntity, String> {
    List<OrganizationBankDetailEntity> findAllByOrganizationEntity_Id(String organizationId);

    @Query("""
        SELECT obd
        FROM OrganizationBankDetailEntity obd
        WHERE obd.organizationEntity.id = :organizationId
          AND obd.bankDetailEntity.activationStatus = 'APPROVED'
        """)
    OrganizationBankDetailEntity findApprovedBankDetailByOrganizationId(String organizationId);
}
