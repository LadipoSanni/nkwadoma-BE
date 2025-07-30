package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface LoaneeRepository extends JpaRepository<LoaneeEntity,String> {
    LoaneeEntity findLoaneeByUserIdentityEmail(String email);

    Optional<LoaneeEntity> findLoaneeByUserIdentityId(String userId);

    @Query("SELECT l FROM CohortLoaneeEntity l WHERE l.cohort.id = :cohortId AND l.id IN :loaneeIds")
    List<LoaneeEntity> findAllLoaneesByCohortIdAndLoaneeIds(
            @Param("cohortId") String cohortId,
            @Param("loaneeIds") List<String> loaneeIds
    );

    @Query("""
        select l from LoaneeEntity l
                join CohortLoaneeEntity cle on  cle.loanee.id = l.id
                        where cle.cohort.id = :id 
                                """)

    List<LoaneeEntity> findAllLoaneesByCohortId(String id);

    @Query("""
        SELECT l FROM CohortLoaneeEntity l
            
        WHERE l.cohort.id = :cohortId
        AND (upper(concat(l.loanee.userIdentity.firstName, ' ', l.loanee.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
        OR upper(concat(l.loanee.userIdentity.lastName, ' ', l.loanee.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%')))
        AND (:status IS NULL OR l.loaneeStatus = :status)
        AND (:uploadedStatus IS NULL OR l.loanee.uploadedStatus = :uploadedStatus)
        AND l.loaneeStatus != 'ARCHIVE'
    """)
    Page<LoaneeEntity> findByCohortIdAndNameFragment(@Param("cohortId") String cohortId,
                                                     @Param("nameFragment") String nameFragment,
                                                     @Param("status") LoaneeStatus status,
                                                     @Param("uploadedStatus") UploadedStatus uploadedStatus,
                                                     Pageable pageable);



    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM CohortLoaneeEntity l 
            JOIN CohortEntity c ON l.cohort.id = c.id 
            JOIN ProgramEntity p ON c.programId = p.id 
            WHERE l.id = :loaneeId AND p.organizationIdentity.id = :organizationId
                        """)
    boolean checkIfLoaneeCohortExistInOrganization(@Param("loaneeId") String loaneeId,
                                                   @Param("organizationId") String organizationId);


}
