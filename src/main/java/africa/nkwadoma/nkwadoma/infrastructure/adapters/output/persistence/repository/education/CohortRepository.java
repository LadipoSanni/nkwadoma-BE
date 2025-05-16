package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CohortRepository extends JpaRepository<CohortEntity, String> {
    Page<CohortEntity> findByNameContainingIgnoreCase(String name,Pageable pageRequest);


    @Query("SELECT c FROM CohortEntity c WHERE c.programId = :programId " +
            "AND c.cohortStatus IS NOT NULL " +
            "AND (:cohortStatus IS NULL OR c.cohortStatus = :cohortStatus)")
    Page<CohortEntity> findAllByProgramIdAndCohortStatus(
            @Param("programId") String programId,
            @Param("cohortStatus") CohortStatus cohortStatus,
            Pageable pageRequest);

    @Query("""
    SELECT 
        c.id AS id,
        c.name AS name,
        COALESCE(COUNT(DISTINCT lne.id), 0) AS numberOfLoanees,
        c.startDate AS startDate,
        COALESCE(SUM(lr.loanAmountRequested), 0) AS amountRequested,
        c.tuitionAmount AS tuitionAmount,
        COALESCE(0, 0) AS amountReceived,
        COALESCE(0, 0) AS amountOutstanding
    FROM CohortEntity c
    LEFT JOIN LoaneeEntity lne ON lne.cohortId = c.id
    LEFT JOIN LoanEntity le ON le.loaneeEntity.id = lne.id AND le.loanOfferId IS NOT NULL
    LEFT JOIN LoanOfferEntity lo ON lo.id = le.loanOfferId
    LEFT JOIN LoanRequestEntity lr ON lr.id = lo.loanRequest.id
    WHERE c.organizationId = :organizationId AND c.cohortStatus = :cohortStatus
    GROUP BY c.id, c.name, c.startDate
""")
    Page<CohortProjection> findAllByOrganizationIdAndCohortStatus(@Param("organizationId") String organizationId,
                                                                  Pageable pageRequest, @Param("cohortStatus")
                                                                  CohortStatus cohortStatus);

    Page<CohortEntity> findByProgramIdAndNameContainingIgnoreCase(String programId, String name,Pageable pageRequest);

    CohortEntity findByName(String name);

    Page<CohortEntity> findByOrganizationIdAndNameContainingIgnoreCase(String organizationId, String name,Pageable pageRequest);

    List<CohortEntity> findAllByProgramId(String id);
}
