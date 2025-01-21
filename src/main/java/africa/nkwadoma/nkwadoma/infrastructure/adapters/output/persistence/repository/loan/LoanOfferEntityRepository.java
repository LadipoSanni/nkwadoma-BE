package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface LoanOfferEntityRepository extends JpaRepository<LoanOfferEntity,String> {


    @Query("""
        SELECT lo 
        FROM LoanOfferEntity lo
        JOIN lo.loanee l
        JOIN CohortEntity c ON l.cohortId = c.id
        JOIN ProgramEntity p ON c.programId = p.id
        JOIN p.organizationIdentity o
        WHERE o.id = :organizationId
    """)
    Page<LoanOfferEntity> findAllLoanOfferInOrganization(@Param("organizationId")String organization, Pageable pageRequest);

    @Query("""
          select
                 lo.id as id,lo.loanOfferStatus as loanOfferStatus,lo.dateTimeOffered as dateTimeOffered, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, 
                     l.userIdentity.alternateContactAddress as alternateContactAddress,
                 l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, l.id as loaneeId,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as startDate, c.tuitionAmount as tuitionAmount, c.name as cohortName, l.userIdentity.image as loaneeImage,
                 n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName, n.contactAddress as nextOfKinContactAddress,
                 n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber, n.nextOfKinRelationship as nextOfKinRelationship,
                 l.loaneeLoanDetail.amountRequested as amountRequested,
                 l.userIdentity.gender as gender, l.userIdentity.maritalStatus as maritalStatus,
                 l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
                 l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence,l.userIdentity.email as email,l.userIdentity.phoneNumber as phoneNumber

          from LoanOfferEntity lo
          join LoaneeEntity l on lo.loanee.id = l.id
          left join CohortEntity c on l.cohortId = c.id
          left join NextOfKinEntity n on l.id = n.loaneeEntity.id
          where lo.id = :id
    """)
    LoanOfferProjection findLoanOfferById(@Param("id") String loanOfferId);

    Page<LoanOfferProjection> findAllLoanOffer(Pageable pageRequest);
}
