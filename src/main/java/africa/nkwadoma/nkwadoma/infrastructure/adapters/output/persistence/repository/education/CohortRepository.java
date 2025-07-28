package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CohortRepository extends JpaRepository<CohortEntity, String> {

    @Query("""
    SELECT
        c.id AS id,
        c.name AS name,
        COALESCE(COUNT(DISTINCT lne.id), 0) AS numberOfLoanees,
        c.startDate AS startDate,
        cl.amountRequested as amountRequested,
        c.tuitionAmount AS tuitionAmount,
        cl.amountReceived AS amountReceived,
        cl.outstandingAmount AS amountOutstanding,
        cl.amountRepaid AS amountRepaind
    FROM CohortEntity c
    LEFT JOIN CohortLoaneeEntity cle ON cle.cohort.id = c.id
    LEFT JOIN CohortLoanDetailEntity cl ON cl.cohort.id = c.id
    LEFT JOIN LoanReferralEntity lfr ON lfr.cohortLoanee.id = cle.id
    LEFT JOIN LoanOfferEntity lo ON lo.id = lfr.id
    LEFT JOIN LoaneeEntity lne ON lne.id = cle.loanee.id
    LEFT JOIN LoaneeLoanDetailEntity lld ON lld.id = cle.loaneeLoanDetail.id
    LEFT JOIN LoanEntity le ON le.loanOfferId = lo.id
    WHERE c.organizationId = :organizationId 
        AND (:cohortStatus IS NULL OR c.cohortStatus = :cohortStatus)
        AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
    GROUP BY c.id, c.name, c.startDate,c.tuitionAmount, cl.amountReceived ,cl.outstandingAmount,
        cl.amountRepaid,cl.amountRequested

""")
    Page<CohortProjection> findByNameContainingIgnoreCaseAndOrganizationId(
            @Param("name") String name,
            @Param("cohortStatus") CohortStatus cohortStatus,
            @Param("organizationId") String organizationId,
            Pageable pageRequest);


    @Query("""
       SELECT
        c.id AS id,
        c.name AS name,
        c.cohortDescription as cohortDescription,
        c.programId as programId,
        c.organizationId as organizationId,
        pr.name as programName,
        c.activationStatus as activationStatus,
        c.cohortStatus as cohortStatus,
        c.stillInTraining as stillInTraining,
        c.numberOfLoanRequest as numberOfLoanRequest,
        c.imageUrl as imageUrl,
        c.numberOfLoanees as numberOfLoanees,
        c.startDate AS startDate,
        c.tuitionAmount as tuitionAmount,
        cl.amountReceived AS amountReceived,
        cl.outstandingAmount AS amountOutstanding,
        cl.amountRepaid AS amountRepaid,
        cl.amountRequested as amountRequested
    FROM CohortEntity c
    LEFT JOIN CohortLoanDetailEntity cl ON cl.cohort.id = c.id
    LEFT JOIN ProgramEntity pr ON pr.id = c.programId
    LEFT JOIN CohortLoaneeEntity cle ON cle.cohort.id = c.id
    LEFT JOIN LoanReferralEntity lfr ON lfr.cohortLoanee.id = cle.id
    LEFT JOIN LoanOfferEntity lo ON lo.id = lfr.id
    LEFT JOIN LoaneeEntity lne ON lne.id = cle.loanee.id
    LEFT JOIN LoaneeLoanDetailEntity lld ON lld.id = cle.loaneeLoanDetail.id
    LEFT JOIN LoanEntity le ON le.id = lo.id
    WHERE c.programId = :programId
            AND (:cohortStatus IS NULL OR c.cohortStatus = :cohortStatus)
    GROUP BY c.id, c.name ,c.cohortDescription ,c.programId,
        c.organizationId ,pr.name,c.activationStatus ,c.cohortStatus,c.stillInTraining,
        c.numberOfLoanRequest,c.imageUrl ,c.numberOfLoanees,c.startDate ,c.tuitionAmount,
        cl.amountReceived,cl.outstandingAmount,cl.amountRepaid, cl.amountRequested
""")
    Page<CohortProjection> findAllByProgramIdAndCohortStatus(
            @Param("programId") String programId,
            @Param("cohortStatus") CohortStatus cohortStatus,
            Pageable pageRequest);


    @Query("""
    SELECT
        c.id AS id,
        c.name AS name,
        c.cohortDescription as cohortDescription,
        c.programId as programId,
        c.organizationId as organizationId,
        pr.name as programName,
        c.activationStatus as activationStatus,
        c.cohortStatus as cohortStatus,
        c.stillInTraining as stillInTraining,
        c.numberOfLoanRequest as numberOfLoanRequest,
        c.imageUrl as imageUrl,
        c.numberOfLoanees as numberOfLoanees,
        c.startDate AS startDate,
        c.tuitionAmount as tuitionAmount,
        cl.amountReceived AS amountReceived,
        cl.outstandingAmount AS amountOutstanding,
        cl.amountRepaid AS amountRepaid,
        cl.amountRequested as amountRequested
    FROM CohortEntity c
    LEFT JOIN CohortLoanDetailEntity cl ON cl.cohort.id = c.id
    LEFT JOIN ProgramEntity pr ON pr.id = c.programId
    LEFT JOIN CohortLoaneeEntity cle ON cle.cohort.id = c.id
    LEFT JOIN LoanReferralEntity lfr ON lfr.cohortLoanee.id = cle.id
    LEFT JOIN LoanOfferEntity lo ON lo.id = lfr.id
    LEFT JOIN LoaneeEntity lne ON lne.id = cle.loanee.id
    LEFT JOIN LoaneeLoanDetailEntity lld ON lld.id = cle.loaneeLoanDetail.id
    LEFT JOIN LoanEntity le ON le.id = lo.id
    WHERE pr.organizationIdentity.id = :organizationId
        AND (:cohortStatus IS NULL OR c.cohortStatus = :cohortStatus)
    GROUP BY c.id, c.name ,c.cohortDescription ,c.programId,
        c.organizationId ,pr.name,c.activationStatus ,c.cohortStatus,c.stillInTraining,
        c.numberOfLoanRequest,c.imageUrl ,c.numberOfLoanees,c.startDate ,c.tuitionAmount,
        cl.amountReceived,cl.outstandingAmount,cl.amountRepaid, cl.amountRequested
""")
    Page<CohortProjection> findAllByOrganizationIdAndCohortStatus(
            @Param("organizationId") String organizationId,
            Pageable pageRequest,
            @Param("cohortStatus") CohortStatus cohortStatus);

    Page<CohortEntity> findByProgramIdAndNameContainingIgnoreCase(String programId, String name,Pageable pageRequest);

    CohortEntity findByName(String name);

    Page<CohortEntity> findByOrganizationIdAndNameContainingIgnoreCase(String organizationId, String name,Pageable pageRequest);

    List<CohortEntity> findAllByProgramId(String id);

    @Modifying
    @Query("DELETE FROM CohortEntity c WHERE c.programId = :id")
    int deleteAllCohortAssociateWithProgramIdAndGetCount(@Param("id") String id);;
}
