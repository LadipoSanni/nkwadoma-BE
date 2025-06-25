package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

import java.util.List;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
    @Query("""
        select lre.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName,
               l.userIdentity.alternatePhoneNumber as alternatePhoneNumber,
               l.userIdentity.isIdentityVerified as identityVerified ,
               l.userIdentity.alternateEmail as alternateEmail, l.id as loaneeId,
               l.userIdentity.alternateContactAddress as alternateContactAddress,
               c.name as cohortName, p.name as programName, c.startDate as cohortStartDate,
               lre.loanReferralStatus as status, o.name as referredBy,
               c.tuitionAmount as tuitionAmount, l.userIdentity.image as loaneeImage
        from LoanReferralEntity lre
        join CohortLoaneeEntity  cle on  cle.id = lre.cohortLoanee.id
        join LoaneeEntity l on cle.loanee.id = l.id
        join CohortEntity c on cle.cohort.id = c.id
        join ProgramEntity p on c.programId = p.id
        join OrganizationEntity o on p.organizationIdentity.id = o.id
        where lre.id = :id
    """)
    Optional<LoanReferralProjection> findLoanReferralById(@Param("id") String id);

    List<LoanReferralEntity> findAllByCohortLoanee_Loanee_UserIdentity_id(String userId);

    @Query("""
    select l from LoanReferralEntity l
        join CohortLoaneeEntity cle on cle.loanee.id = :loaneeId
            join CohortLoaneeEntity  c on cle.cohort.id = :cohortId
                where cle.loanee.id = :id
    """)
    LoanReferralEntity findByLoaneeEntityIdAndLoaneeEntityCohortId(@Param("loaneeId") String id,@Param("cohortId") String cohortId);

    Optional<LoanReferralEntity> findAllByCohortLoanee_Loanee_UserIdentity_Email(String loaneeEmail);

    Optional<LoanReferralEntity> findLoanReferralByCohortLoaneeId(String id);
}
