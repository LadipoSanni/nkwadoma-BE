package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ProgramRepository extends JpaRepository<ProgramEntity, String> {
    Page<ProgramEntity> findByNameContainingIgnoreCase(String programName,Pageable pageable);
    Page<ProgramEntity> findByNameContainingIgnoreCaseAndOrganizationIdentityId(String programName, String organizationId, Pageable pageable);
    List<ProgramEntity> findProgramEntitiesByOrganizationIdentityId(String organizationIdentityId);
    @Query("SELECT COUNT(p) > 0 " +
            "FROM ProgramEntity p " +
            "WHERE LOWER(p.name) = LOWER(:programName) " +
            "AND p.organizationIdentity.id = :organizationId " +
            "AND (:programId IS NULL OR p.id != :programId)")
    boolean existsByNameIgnoreCaseAndOrganizationIdentityId(@Param("programName") String programName,
                                                            @Param("organizationId") String organizationId,
                                                            @Param("programId") String programId);

    @Query("""
   
       SELECT p.id as id,
                   pd.amountReceived as totalAmountDisbursed,
                   pd.amountRequested as totalAmountRequested,
                   pd.amountRepaid as totalAmountRepaid,
                   pd.outstandingAmount as totalAmountOutstanding,
                   p.name as name,
                   p.objectives as objectives,
                   p.programDescription as programDescription,
                   p.programStartDate as programStartDate,
                   p.programStatus as programStatus,
                   p.mode as mode,
                   p.duration as duration,
                   p.deliveryType as deliveryType,
                   p.numberOfCohort  as numberOfCohort,
                   p.numberOfLoanees as numberOfLoanees
                       
                   from OrganizationEntity  o 
                   join ProgramEntity p on p.organizationIdentity.id = o.id
                   left join ProgramLoanDetailEntity pd on pd.program.id = p.id
                       
                   where o.id = :organizationId    order by p.createdAt asc 
    """)
    Page<ProgramProjection> findAllByOrganizationIdentityId(@Param("organizationId") String organizationId, Pageable pageable);


    @Query("""
        SELECT COUNT(cle) >= 1
        
        from ProgramEntity p
             join CohortEntity cohort on cohort.programId = p.id
             join CohortLoaneeEntity cle on cle.cohort.id = cohort.id
             where p.id = :programId
        """)
    boolean checkIfLaoneeExistsByProgramId(@Param("programId") String programId);
}
