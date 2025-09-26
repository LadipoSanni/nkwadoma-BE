package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanOfferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface LoanOfferEntityRepository extends JpaRepository<LoanOfferEntity,String> {


    @Query("""
        SELECT lo.id as id,
              l.userIdentity.firstName as firstName,
              l.userIdentity.lastName as lastName,
              lo.dateTimeOffered as dateTimeOffered,
              cle.loaneeLoanDetail.amountRequested as amountRequested,
              lo.amountApproved as amountApproved,
              lp.name as loanProductName,
              lo.loaneeResponse as loaneeResponse,
               CASE
                  WHEN lo.loaneeResponse IS NOT NULL AND lo.loanOfferStatus != 'WITHDRAW'
                  THEN CAST(lo.loaneeResponse AS string)
                  ELSE CAST(lo.loanOfferStatus AS string)
              END as status

    from LoanOfferEntity lo
    join LoanRequestEntity lr on lr.id = lo.id
    join LoanReferralEntity lre on lre.id = lr.id
    join CohortLoaneeEntity cle on cle.id = lre.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    join UserEntity u on u.id = l.userIdentity.id
    join CohortEntity c on cle.cohort.id = c.id
    join ProgramEntity p on c.programId = p.id
    left join LoanProductEntity lp on lo.loanProduct.id = lp.id
    left join NextOfKinEntity n on u.nextOfKinEntity.id = n.id
    join OrganizationEntity o on o.id = p.organizationIdentity.id
    WHERE o.id = :organizationId
            and not exists (
                      select 1 from LoanEntity loan where loan.loanOfferId = lo.id
                  )
        order by lo.dateTimeOffered desc

    """)
    Page<LoanOfferProjection> findAllLoanOfferInOrganization(@Param("organizationId")String organizationId, Pageable pageRequest);

    @Query("""
    select lo.id as id, lo.loanOfferStatus as loanOfferStatus, lo.dateTimeOffered as dateTimeOffered,
           l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, l.userIdentity.levelOfEduction as levelOfEducation,
           l.creditScore as creditScore, l.userIdentity.gender as gender, l.userIdentity.phoneNumber as phoneNumber,
           l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.alternateContactAddress as alternateContactAddress,
           l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber,
           l.id as loaneeId, cle.loaneeLoanDetail.initialDeposit as initialDeposit, l.userIdentity.maritalStatus as maritalStatus,
           l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
           l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence,
           l.userIdentity.email as email, cle.loaneeLoanDetail.amountRequested as amountRequested,
           lo.amountApproved as amountApproved, c.startDate as startDate, c.tuitionAmount as tuitionAmount, c.name as cohortName,
           l.userIdentity.image as loaneeImage, p.name as programName, lp.termsAndCondition as termsAndCondition,
           n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName,
           n.contactAddress as nextOfKinContactAddress, n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber,
           n.nextOfKinRelationship as nextOfKinRelationship, lp.name as loanProductName, lr.id as loanRequestId,
           lp.id as loanProductId, lo.loaneeResponse as loaneeResponse, cle.referredBy as loanRequestReferredBy,
           c.id as cohortId,cle.referredBy as referredBy, cle.id as cohortLoaneeId
    from LoanOfferEntity lo
    join LoanRequestEntity lr on lr.id = lo.id
    join LoanReferralEntity lre on lre.id = lr.id
    join CohortLoaneeEntity cle on cle.id = lre.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    join UserEntity u on u.id = l.userIdentity.id
    join CohortEntity c on cle.cohort.id = c.id
    join ProgramEntity p on c.programId = p.id
    left join LoanProductEntity lp on lo.loanProduct.id = lp.id
    left join NextOfKinEntity n on u.nextOfKinEntity.id = n.id
    where lo.id = :id
""")
    LoanOfferProjection findLoanOfferById(@Param("id") String loanOfferId);


    @Query("""
       select lo.id as id,
              l.userIdentity.firstName as firstName,
              l.userIdentity.lastName as lastName,
              lo.dateTimeOffered as dateTimeOffered,
              cle.loaneeLoanDetail.amountRequested as amountRequested,
              lo.amountApproved as amountApproved,
              lp.name as loanProductName,
              lo.loaneeResponse as loaneeResponse,
               CASE
                  WHEN lo.loaneeResponse IS NOT NULL AND lo.loanOfferStatus != 'WITHDRAW'
                  THEN CAST(lo.loaneeResponse AS string)
                  ELSE CAST(lo.loanOfferStatus AS string)
              END as status
 
    from LoanOfferEntity lo
    join LoanRequestEntity lr on lr.id = lo.id
    join LoanReferralEntity lre on lre.id = lr.id
    join CohortLoaneeEntity cle on cle.id = lre.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    join UserEntity u on u.id = l.userIdentity.id
    join CohortEntity c on cle.cohort.id = c.id
    join ProgramEntity p on c.programId = p.id
    left join LoanProductEntity lp on lo.loanProduct.id = lp.id

       where not exists (
                 select 1 from LoanEntity loan where loan.loanOfferId = lo.id
             )
       order by lo.dateTimeOffered desc
    """)
    Page<LoanOfferProjection> findAllLoanOffer(Pageable pageRequest);


    @Query("""
      SELECT lo.id AS id,
             l.userIdentity.firstName AS firstName,
             l.userIdentity.lastName AS lastName,
             lo.dateTimeOffered AS dateTimeOffered,
             cle.loaneeLoanDetail.amountRequested AS amountRequested,
             lo.amountApproved AS amountApproved,
             lp.name AS loanProductName,
             lo.loaneeResponse AS loaneeResponse,
             CASE
                 WHEN lo.loaneeResponse IS NOT NULL AND lo.loanOfferStatus != 'WITHDRAW'
                 THEN CAST(lo.loaneeResponse AS string)
                 ELSE CAST(lo.loanOfferStatus AS string)
             END AS status
      FROM LoanOfferEntity lo
      JOIN LoanRequestEntity lr ON lr.id = lo.id
      JOIN LoanReferralEntity lre ON lre.id = lr.id
      JOIN CohortLoaneeEntity cle ON cle.id = lre.cohortLoanee.id
      JOIN LoaneeEntity l ON l.id = cle.loanee.id
      JOIN UserEntity u ON u.id = l.userIdentity.id
      JOIN CohortEntity c ON cle.cohort.id = c.id
      JOIN ProgramEntity p ON c.programId = p.id
      JOIN OrganizationEntity o ON o.id = p.organizationIdentity.id
      LEFT JOIN LoanProductEntity lp ON lo.loanProduct.id = lp.id
      WHERE (:programId IS NULL OR p.id = :programId)
                AND (:organizationId IS NULL OR o.id = :organizationId)
      AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))

      AND NOT EXISTS (
          SELECT 1 FROM LoanEntity loan WHERE loan.loanOfferId = lo.id
      )
      ORDER BY lo.dateTimeOffered DESC
    """)
    Page<LoanOfferProjection> findAllLoanOfferByLoaneeNameInOrganizationAndProgram(
            @Param("programId") String programId,
            @Param("organizationId") String organizationId,
            @Param("name") String name,
            Pageable pageRequest
    );

    @Query("""
    SELECT lo.id AS id,
           u.firstName AS firstName,
           u.lastName AS lastName,
           lo.dateTimeOffered AS dateTimeOffered,
           cle.loaneeLoanDetail.amountRequested AS amountRequested,
           lo.amountApproved AS amountApproved,
           lp.name AS loanProductName,
           lo.loaneeResponse as loaneeResponse
   
    from LoanOfferEntity lo
    join LoanRequestEntity lr on lr.id = lo.id
    join LoanReferralEntity lre on lre.id = lr.id
    join CohortLoaneeEntity cle on cle.id = lre.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    join UserEntity u on u.id = l.userIdentity.id
    join CohortEntity c on cle.cohort.id = c.id
    join ProgramEntity p on c.programId = p.id
    left join LoanProductEntity lp on lo.loanProduct.id = lp.id
    left join NextOfKinEntity n on u.nextOfKinEntity.id = n.id
    join OrganizationEntity o on o.id = p.organizationIdentity.id
    WHERE 
        c.programId = :programId
        AND o.id = :organizationId
        AND not exists (
                      select 1 from LoanEntity loan where loan.loanOfferId = lo.id
                  )
    order by lo.dateTimeOffered desc
    """)
    Page<LoanOfferProjection> filterLoanOfferByProgramIdAndOrganization(@Param("programId") String programId,
                                                                        @Param("organizationId") String organizationId,
                                                                        Pageable pageRequest);

    @Query("""
    SELECT lo from LoanOfferEntity lo
 
            join LoanRequestEntity  lr on lr.id = lo.id
            join LoanReferralEntity lre on lre.id = lr.id
            join CohortLoaneeEntity cle on cle.loanee.id = lre.cohortLoanee.id
    """)
    LoanOfferEntity findLoanOfferByLoaneeId(String loaneeId);


    @Query("""
    select lo.id as id, lo.loanOfferStatus as loanOfferStatus, lo.dateTimeOffered as dateTimeOffered,
           l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName,
           l.creditScore as creditScore, l.userIdentity.gender as gender, l.userIdentity.phoneNumber as phoneNumber,
           l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.alternateContactAddress as alternateContactAddress,
           l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber,
           l.id as loaneeId, cle.loaneeLoanDetail.initialDeposit as initialDeposit, l.userIdentity.maritalStatus as maritalStatus,
           l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
           l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence,
           l.userIdentity.email as email, cle.loaneeLoanDetail.amountRequested as amountRequested,
           lo.amountApproved as amountApproved, c.startDate as startDate, c.tuitionAmount as tuitionAmount, c.name as cohortName,
           l.userIdentity.image as loaneeImage, p.name as programName, lp.termsAndCondition as termsAndCondition,
           n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName,
           n.contactAddress as nextOfKinContactAddress, n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber,
           n.nextOfKinRelationship as nextOfKinRelationship, lp.name as loanProductName, lr.id as loanRequestId,
           lp.id as loanProductId, lo.loaneeResponse as loaneeResponse, cle.referredBy as referredBy, cle.id as cohortLoaneeId
    from LoanOfferEntity lo
    join LoanRequestEntity lr on lr.id = lo.id
    join LoanReferralEntity lre on lre.id = lr.id
    join CohortLoaneeEntity cle on cle.id = lre.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    join UserEntity u on u.id = l.userIdentity.id
    join CohortEntity c on cle.cohort.id = c.id
    join ProgramEntity p on c.programId = p.id
    left join LoanProductEntity lp on lo.loanProduct.id = lp.id
    left join NextOfKinEntity n on u.nextOfKinEntity.id = n.id
    where u.id = :userId
    """)
    Page<LoanOfferProjection> finAllLoanOfferAssignedToLoaneeByUserId(@Param("userId") String userId, Pageable pageRequest);


        @Query("""
    SELECT COUNT(*)
    FROM LoanOfferEntity lo
    JOIN LoanRequestEntity lr ON lr.id = lo.id
    JOIN LoanReferralEntity lre ON lre.id = lr.id
    JOIN LoanProductEntity lp ON lp.id = lo.loanProduct.id
    JOIN CohortLoaneeEntity cle ON cle.id = lre.cohortLoanee.id
    JOIN LoaneeEntity l ON l.id = cle.loanee.id
    JOIN CohortEntity c ON c.id = cle.cohort.id
    WHERE c.id = :cohortId
    AND NOT EXISTS (
        SELECT 1 FROM LoanEntity loan WHERE loan.loanOfferId = lo.id
)
""")
    int countPendingOfferByCohortId(@Param("cohortId") String cohortId);


    @Query("""
    SELECT COUNT(*)
    FROM LoanOfferEntity lo
    JOIN LoanRequestEntity lr ON lr.id = lo.id
    JOIN LoanReferralEntity lre ON lre.id = lr.id
    JOIN LoanProductEntity lp ON lp.id = lo.loanProduct.id
    JOIN CohortLoaneeEntity cle ON cle.id = lre.cohortLoanee.id
    JOIN LoaneeEntity l ON l.id = cle.loanee.id
    JOIN CohortEntity c ON c.id = cle.cohort.id
    JOIN ProgramEntity p ON p.id = c.programId
    join OrganizationEntity o ON o.id = p.organizationIdentity.id
    WHERE o.id = :organizationId
    AND NOT EXISTS (
        SELECT 1 FROM LoanEntity loan WHERE loan.loanOfferId = lo.id
)
""")
    int countPendingOfferByOrganizationId(@Param("organizationId") String organizationId);
}
