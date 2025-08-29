package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CohortLoaneeRepository extends JpaRepository<CohortLoaneeEntity, String> {


    @Query("""
        select cohort_loanee.id as id,
                user.firstName as firstName, user.lastName as lastName,user.gender as gender,
                user.stateOfOrigin as stateOfOrigin, user.dateOfBirth as dateOfBirth, user.email as email,
                user.residentialAddress as residentialAddress, user.maritalStatus as maritalStatus,
                user.phoneNumber as phoneNumber,user.alternateEmail as alternateEmail,
                user.alternatePhoneNumber as alternatePhoneNumber, user.alternateContactAddress as alternateContactAddress,
                user.stateOfResidence as stateOfResidence, user.nationality as nationality,
                next_of_kin.nextOfKinRelationship as nextOfKinRelationship,next_of_kin.phoneNumber as nextOfKinPhoneNumber,
                next_of_kin.firstName as nextOfKinFirstName, next_of_kin.lastName as nextOfKinLastName,
                next_of_kin.contactAddress as nextOfKinResidentialAddress,
                program.name as programName, organization.name as organizationName,
                loan_offer.amountApproved as amountReceived,loan_product.interestRate as interestRate,
                COALESCE(SUM(repayment_history.amountPaid), 0) AS amountPaid,
                loan_offer.amountApproved - COALESCE(SUM(repayment_history.amountPaid), 0) AS amountOutstanding,
                (CASE WHEN loan_offer.amountApproved = 0 THEN NULL
                ELSE ROUND((COALESCE(SUM(repayment_history.amountPaid), 0) / loan_offer.amountApproved * 100), 8) END) AS repaymentPercentage,
                (CASE WHEN loan_offer.amountApproved = 0 THEN NULL
                ELSE ROUND(((loan_offer.amountApproved - COALESCE(SUM(repayment_history.amountPaid), 0)) / loan_offer.amountApproved * 100), 8) END) AS debtPercentage,
                cohort.name as cohortName , loaneeLoanDetail.id as loaneeLoanDetailId, loaneeLoanDetail.interestIncurred as interestIncurred
                 
                       
                from CohortLoaneeEntity cohort_loanee
    
                left join LoaneeEntity loanee on loanee.id = cohort_loanee.loanee.id
                left join UserEntity user on user.id = loanee.userIdentity.id
                left join NextOfKinEntity next_of_kin on next_of_kin.id = user.nextOfKinEntity.id    
                left join CohortEntity cohort on cohort.id = cohort_loanee.cohort.id
                left join ProgramEntity program on program.id = cohort.programId
                left join OrganizationEntity organization on organization.id = program.organizationIdentity.id      
                left join LoanReferralEntity  loan_referral on loan_referral.cohortLoanee.id = cohort_loanee.id
                left join LoanRequestEntity loan_request on loan_request.id = loan_referral.id 
                left join LoanOfferEntity loan_offer on loan_offer.id = loan_request.id
                left join LoanProductEntity loan_product on loan_product.id = loan_offer.loanProduct.id
                left join RepaymentHistoryEntity repayment_history on repayment_history.loanee.id = loanee.id
                left join LoaneeLoanDetailEntity loaneeLoanDetail on loaneeLoanDetail.id = cohort_loanee.loaneeLoanDetail.id
                
                where loanee.id = :loaneeId and cohort.id = :cohortId   
               GROUP BY cohort_loanee.id,
                        user.firstName, user.lastName,user.gender,user.stateOfOrigin,
                        user.dateOfBirth,user.email,user.residentialAddress,
                        user.maritalStatus,user.phoneNumber,user.alternateEmail,
                        user.alternatePhoneNumber,user.alternateContactAddress,
                        user.stateOfResidence,user.nationality,next_of_kin.nextOfKinRelationship,
                        next_of_kin.phoneNumber,next_of_kin.firstName,next_of_kin.lastName,
                        next_of_kin.contactAddress, program.name,
                        organization.name,loan_offer.amountApproved, loan_product.interestRate,cohort.name, 
                        loaneeLoanDetail.id, loaneeLoanDetail.interestIncurred     
    """)
    CohortLoaneeProjection findCohortLoaneeEntityByLoanee_IdAndCohort_Id(@Param("loaneeId") String loaneeId,@Param("cohortId") String cohortId);

    CohortLoaneeEntity findCohortLoaneeEntityByCohort_ProgramIdAndLoanee_Id(String cohortProgramId, String loaneeId);

    @Query("""
        SELECT cohort_loanee FROM CohortLoaneeEntity cohort_loanee 
            WHERE cohort_loanee.cohort.id = :cohortId AND cohort_loanee.loanee.id  IN :loaneeIds
    """)
    List<CohortLoaneeEntity> findAllCohortLoaneeEntityBy_CohortIdAnd_ListOfLoaneeId(@Param("cohortId") String cohortId,
                                                                                    @Param("loaneeIds") List<String> loaneeIds);

    @Query("""
        SELECT COUNT(cohort_loanee) FROM CohortLoaneeEntity cohort_loanee
        WHERE cohort_loanee.loanee.id = :loaneeId AND cohort_loanee.loaneeStatus = 'REFERRED'
""")
    Long countByLoaneeIdAndStatus(@Param("loaneeId") String loaneeId);


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

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE CohortLoaneeEntity cle SET cle.loaneeStatus = :loaneeStatus
        WHERE cle.cohort.id = :cohortId and cle.loanee.id IN (:loaneeIds)
 """)
    void updateStatusByIds(@Param("cohortId") String cohortId,
                           @Param("loaneeIds") List<String> loaneeIds,
                           @Param("loaneeStatus") LoaneeStatus loaneeStatus);

    @Query("""
    
       select cl from CohortLoaneeEntity cl
       JOIN LoanReferralEntity lr on lr.cohortLoanee.id = cl.id
       join LoanRequestEntity lre on lre.id = lre.id
   
      where lre.id = : id
    """)
    CohortLoaneeEntity findCohortLoaneeByLoanRequestId(@Param("id") String id);

    @Query("""
    
       select cl from CohortLoaneeEntity cl
       JOIN LoaneeLoanDetailEntity  lld on lld.id = cl.loaneeLoanDetail.id
          
       where lld.id = :id  and lld.amountOutstanding is not null and lld.amountOutstanding > 0
    """)
    CohortLoaneeEntity findByLoaneeLoanDetailId(@Param("id") String id);

    @Query("""
        SELECT COUNT(cohort_loanee) FROM CohortLoaneeEntity cohort_loanee
        WHERE cohort_loanee.loanee.id = :loaneeId
""")
    Long countByLoaneeId(@Param("loaneeId") String loaneeId);


    @Query("""
    
       select cl from CohortLoaneeEntity cl
       JOIN LoanReferralEntity lr on lr.cohortLoanee.id = cl.id
       join LoanRequestEntity lre on lre.id = lre.id
       join LoanOfferEntity  lo on lo.id = lre.id
       join LoanEntity  loan on loan.loanOfferId = lo.id
      where loan.id = : id
    """)
    CohortLoaneeEntity findCohortLoaneeByLoanId(@Param("id") String id);
}
