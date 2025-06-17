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

    @Query("""
    SELECT l FROM LoaneeEntity l
    LEFT JOIN LoanEntity loan ON loan.loaneeEntity.id = l.id
    WHERE l.cohortId = :cohortId 
        AND (:status IS NULL AND l.loaneeStatus != 'ARCHIVE' OR l.loaneeStatus = :status)
    AND (:status IS NULL OR l.loaneeStatus = :status)
    AND (:loanStatus IS NULL OR loan.loanStatus = :loanStatus)
    AND (:uploadedStatus IS NULL OR l.uploadedStatus = :uploadedStatus)
    """)
    Page<LoaneeEntity> findAllByCohortId(@Param("cohortId") String cohortId,
                                         @Param("status")LoaneeStatus status,
                                         @Param("loanStatus") LoanStatus loanStatus,
                                         @Param("uploadedStatus") UploadedStatus uploadedStatus,
                                         Pageable pageable);

    @Query("SELECT l FROM LoaneeEntity l WHERE l.cohortId = :cohortId AND l.id IN :loaneeIds")
    List<LoaneeEntity> findAllLoaneesByCohortIdAndLoaneeIds(
            @Param("cohortId") String cohortId,
            @Param("loaneeIds") List<String> loaneeIds
    );

    List<LoaneeEntity> findAllLoaneesByCohortId(String id);


    @Query("""
        SELECT l FROM LoaneeEntity l
        WHERE l.cohortId = :cohortId
        AND (upper(concat(l.userIdentity.firstName, ' ', l.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
        OR upper(concat(l.userIdentity.lastName, ' ', l.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%')))
        AND (:status IS NULL AND l.loaneeStatus != 'ARCHIVE' OR l.loaneeStatus = :status)
        """)
    Page<LoaneeEntity> findByCohortIdAndNameFragment(@Param("cohortId") String cohortId,
                                                     @Param("nameFragment") String nameFragment,
                                                     @Param("status") LoaneeStatus status,
                                                     Pageable pageable)  ;

    @Query("""
        SELECT l.id as id,
               l.userIdentity.firstName as firstName,
               l.userIdentity.lastName as lastName,
               lr.referredBy as instituteName
      
        FROM LoanEntity loan
                Join loan.loaneeEntity l
                join LoanOfferEntity lo ON lo.id = loan.loanOfferId
                join LoanRequestEntity lr ON lr.id = lo.loanRequest.id
                where
                        lo.loanProduct.id = :loanProductId
        """)
    Page<LoaneeProjection> findAllByLoanProductId( @Param("loanProductId")String loanProductId, Pageable pageRequest);

    @Query("""
        SELECT l.id as id,
               l.userIdentity.firstName as firstName,
               l.userIdentity.lastName as lastName,
               lr.referredBy as instituteName
      
        FROM LoanEntity loan
                Join loan.loaneeEntity l
                join LoanOfferEntity lo ON lo.id = loan.loanOfferId
                join LoanRequestEntity lr ON lr.id = lo.loanRequest.id
                where
                        lo.loanProduct.id = :loanProductId
                        and (upper(concat(l.userIdentity.firstName, ' ', l.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
                        or upper(concat(l.userIdentity.lastName, ' ', l.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%')))
        """)
    Page<LoaneeProjection> findAllByLoanProductIdAndNameFragment(@Param("loanProductId")String loanProductId, @Param("nameFragment") String nameFragment, Pageable pageRequest);


    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM LoaneeEntity l 
            JOIN CohortEntity c ON l.cohortId = c.id 
            JOIN ProgramEntity p ON c.programId = p.id 
            WHERE l.id = :loaneeId AND p.organizationIdentity.id = :organizationId
                        """)
    boolean checkIfLoaneeCohortExistInOrganization(@Param("loaneeId") String loaneeId,
                                                   @Param("organizationId") String organizationId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE LoaneeEntity l SET l.loaneeStatus = :status
        WHERE l.id IN (:ids)
 """)
    void updateStatusByIds(@Param("ids") List<String> ids, @Param("status") LoaneeStatus status);

}
