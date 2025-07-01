package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CohortLoaneeRepository extends JpaRepository<CohortLoaneeEntity, String> {
    CohortLoaneeEntity findCohortLoaneeEntityByLoanee_IdAndCohort_Id(String loaneeId, String cohortId);

    CohortLoaneeEntity findCohortLoaneeEntityByCohort_ProgramIdAndLoanee_Id(String cohortProgramId, String loaneeId);

    @Query("""
        SELECT cohort_loanee FROM CohortLoaneeEntity cohort_loanee 
            WHERE cohort_loanee.cohort.id = :cohortId AND cohort_loanee.loanee.id  IN :loaneeIds
    """)
    List<CohortLoaneeEntity> findAllCohortLoaneeEntityBy_CohortIdAnd_ListOfLoaneeId(@Param("cohortId") String cohortId,
                                                                                    @Param("loaneeIds") List<String> loaneeIds);

    Long countByLoaneeId(String loaneeId);


    @Query("""
    SELECT CLE FROM CohortLoaneeEntity CLE
    
                WHERE CLE.cohort.id = :cohortId
                AND (:loaneeStatus IS NULL AND CLE.loaneeStatus != 'ARCHIVE' OR CLE.loaneeStatus = :loaneeStatus )
                AND (:loaneeStatus IS NULL OR CLE.loaneeStatus = :loaneeStatus)
                AND (:uploadedStatus IS NULL OR CLE.loanee.uploadedStatus = :uploadedStatus)
    """)
    Page<CohortLoaneeEntity> findAllByCohortId(@Param("cohortId") String cohortId,
                                               @Param("loaneeStatus") LoaneeStatus loaneeStatus,
                                               @Param("uploadedStatus") UploadedStatus uploadedStatus,
                                               Pageable pageRequest);

    @Query("""
        SELECT l FROM CohortLoaneeEntity l
        WHERE l.cohort.id = :cohortId
        AND (upper(concat(l.loanee.userIdentity.firstName, ' ', l.loanee.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
        OR upper(concat(l.loanee.userIdentity.lastName, ' ', l.loanee.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%')))
        AND (:status IS NULL OR l.loaneeStatus = :status)
        AND (:uploadedStatus IS NULL OR l.loanee.uploadedStatus = :uploadedStatus)
        AND l.loaneeStatus != 'ARCHIVE'
    """)
    Page<CohortLoaneeEntity> findByCohortIdAndNameFragment(@Param("cohortId") String cohortId,
                                                     @Param("nameFragment") String nameFragment,
                                                     @Param("status") LoaneeStatus status,
                                                     @Param("uploadedStatus") UploadedStatus uploadedStatus,
                                                     Pageable pageable);


    @Query("""
        select cl from CohortLoaneeEntity cl
        JOIN LoanReferralEntity lr on lr.cohortLoanee.id = cl.id
        JOIN LoanRequestEntity lre on lre.id = lr.id
        JOIN LoanOfferEntity lo on lo.id = lre.id
        where
        lo.loanProduct.id = :loanProductId
    """)
    Page<CohortLoaneeEntity> findAllLoanProductBenficiaryByLoanProductId(@Param("loanProductId")String loanProductId, Pageable pageRequest);


    @Query("""
        select cl from CohortLoaneeEntity cl
        JOIN LoanReferralEntity lr on lr.cohortLoanee.id = cl.id
        JOIN LoanRequestEntity lre on lre.id = lr.id
        JOIN LoanOfferEntity lo on lo.id = lre.id
        
        where
              lo.loanProduct.id = :loanProductId
              and (upper(concat(cl.loanee.userIdentity.firstName, ' ', cl.loanee.userIdentity.lastName)) LIKE upper(concat('%', :nameFragment, '%'))
              or upper(concat(cl.loanee.userIdentity.lastName, ' ', cl.loanee.userIdentity.firstName)) LIKE upper(concat('%', :nameFragment, '%')))
        """)
    Page<CohortLoaneeEntity> searchLoanBeneficiaryByLoanProductId(@Param("loanProductId")String loanProductId, @Param("nameFragment") String nameFragment, Pageable pageRequest);
}
