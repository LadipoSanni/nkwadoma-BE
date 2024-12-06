package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntitiy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanOfferEntityRepository extends JpaRepository<LoanOfferEntitiy,String> {


    @Query("""
        SELECT lo 
        FROM LoanOfferEntitiy lo
        JOIN lo.loanee l
        JOIN CohortEntity c ON l.cohortId = c.id
        JOIN ProgramEntity p ON c.programId = p.id
        JOIN p.organizationEntity o
        WHERE o.id = :organizationId
    """)
    Page<LoanOfferEntitiy> findAllLoanOfferInOrganization(@Param("organizationId")String organization, Pageable pageRequest);
}
