package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ProgramRepository extends JpaRepository<ProgramEntity, String> {
    List<ProgramEntity> findByNameContainingIgnoreCase(String programName);
    List<ProgramEntity> findByNameContainingIgnoreCaseAndOrganizationIdentityId(String programName, String organizationId);
    List<ProgramEntity> findProgramEntitiesByOrganizationIdentityId(String organizationIdentityId);
    boolean existsByNameAndOrganizationIdentity_Id(String programName, String organizationId);
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM ProgramEntity p " +
            "WHERE LOWER(p.name) = LOWER(:programName) " +
            "AND p.organizationIdentity.id = :organizationId")
    boolean existsByNameIgnoreCaseAndOrganizationIdentityId(@Param("programName") String programName,
                                                            @Param("organizationId") String organizationId);


    @Query("""
   
       SELECT p.id as id,
                   pd.totalAmountReceived as totalAmountReceived,
                   pd.totalAmountRequested as totalAmountRequested,
                   pd.totalAmountRepaid as totalDebtRepaid,
                   pd.totalOutstandingAmount as totalCurrentDebt
                       
                   from OrganizationEntity  o 
                   join ProgramEntity p on p.organizationIdentity.id = o.id
                   join ProgramLoanDetailEntity pd on pd.program.id = p.id
                       
                           where o.id = :organizationId    
    """)
    Page<ProgramProjection> findAllByOrganizationIdentityId(String organizationId, Pageable pageable);
}
