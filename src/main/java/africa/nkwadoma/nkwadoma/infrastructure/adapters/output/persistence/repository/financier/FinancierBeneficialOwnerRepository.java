package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierBeneficialOwner;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierBeneficialOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinancierBeneficialOwnerRepository extends JpaRepository<FinancierBeneficialOwnerEntity, String> {
    @Query("SELECT fbo.beneficialOwnerEntity FROM FinancierBeneficialOwnerEntity fbo WHERE fbo.financierEntity.id = :financierId")
    List<BeneficialOwnerEntity> findBeneficialOwnersByFinancierId(@Param("financierId") String financierId);

    List<FinancierBeneficialOwnerEntity> findAllByFinancierEntity_Id(String financierId);
    @Query("""
    SELECT fbo FROM FinancierBeneficialOwnerEntity fbo
    JOIN FETCH fbo.financierEntity
    JOIN FETCH fbo.beneficialOwnerEntity
    WHERE fbo.financierEntity.id = :financierId
""")
    List<FinancierBeneficialOwnerEntity> findAllByFinancierId(@Param("financierId") String financierId);
}
