package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAggregateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoaneeLoanAggregateRepository extends JpaRepository<LoaneeLoanAggregateEntity, String> {

    LoaneeLoanAggregateEntity findByLoaneeId(String id);


    @Query("""
        
        select loaneeLoanAggregate.historicalDebt as historicalDebt ,
                loaneeLoanAggregate.totalAmountOutstanding as totalAmountOutstanding ,
                loaneeLoanAggregate.numberOfLoans as numberOfLoans ,  
                loanee.userIdentity.firstName as firstName ,
                loanee.userIdentity.lastName as lastName ,
                loanee.userIdentity.email as email,
                 loanee.id as loaneeId                   
            from LoaneeLoanAggregateEntity loaneeLoanAggregate
            join LoaneeEntity loanee on loanee.id = loaneeLoanAggregate.loanee.id
           
            order by loaneeLoanAggregate.numberOfLoans desc 

    """)
    Page<LoaneeLoanAggregateProjection> findAllByPagination(Pageable pageRequest);

    @Query("""
        select loaneeLoanAggregate.historicalDebt as historicalDebt,
               loaneeLoanAggregate.totalAmountOutstanding as totalAmountOutstanding,
               loaneeLoanAggregate.numberOfLoans as numberOfLoans,
               loanee.userIdentity.firstName as firstName,
               loanee.userIdentity.lastName as lastName,
               loanee.userIdentity.email as email  ,
               loanee.id as loaneeId
              
        from LoaneeLoanAggregateEntity loaneeLoanAggregate
        join LoaneeEntity loanee on loanee.id = loaneeLoanAggregate.loanee.id
        where loanee.userIdentity.firstName like %:nameFragment% 
           or loanee.userIdentity.lastName like %:nameFragment%
        order by loaneeLoanAggregate.numberOfLoans desc 
""")
    Page<LoaneeLoanAggregateProjection> searchLoaneeLoanAggregate(@Param("nameFragment") String nameFragment, Pageable pageRequest);

    @Query("""
    SELECT
        SUM(l.historicalDebt) AS totalAmountReceived,
        SUM(l.totalAmountOutstanding) AS totalAmountOutstanding,
        SUM(l.totalAmountRepaid) AS totalAmountRepaid,
        COUNT(l) AS numberOfLoanee
    FROM LoaneeLoanAggregateEntity l
    """)
    LoanSummaryProjection getLoanSummary();

    @Query("""
    select l from  LoaneeLoanAggregateEntity l
        join LoaneeEntity loanee on loanee.id = l.loanee.id
        join CohortLoaneeEntity cohortLoanee on cohortLoanee.loanee.id = loanee.id
        join LoaneeLoanDetailEntity loaneeLoanDetail on loaneeLoanDetail.id = cohortLoanee.loaneeLoanDetail.id
        
        where loaneeLoanDetail.id = :id       
    """)
    LoaneeLoanAggregateEntity findByLoaneeLoandetailId(@Param("id") String id);

    @Query("""
        
        select loaneeLoanAggregate.historicalDebt as historicalDebt ,
                loaneeLoanAggregate.totalAmountOutstanding as totalAmountOutstanding ,
                loaneeLoanAggregate.numberOfLoans as numberOfLoans ,  
                loanee.userIdentity.firstName as firstName ,
                loanee.userIdentity.lastName as lastName ,
                loanee.userIdentity.email as email,
                 loanee.id as loaneeId                   
            from LoaneeLoanAggregateEntity loaneeLoanAggregate
            join LoaneeEntity loanee on loanee.id = loaneeLoanAggregate.loanee.id
            join CohortLoaneeEntity cohortLoanee on cohortLoanee.loanee.id = loanee.id
            join CohortEntity  cohort on cohort.id = cohortLoanee.cohort.id
            join ProgramEntity program on program.id = cohort.programId
            join OrganizationEntity organization on organization.id = program.organizationIdentity.id
             
            where organization.id = :organizationId            
              
            order by loaneeLoanAggregate.numberOfLoans desc 

    """)
    Page<LoaneeLoanAggregateProjection> findAllByOrganizationId(@Param("organizationId") String organizationId,Pageable pageRequest);

    @Query("""
        SELECT loaneeLoanAggregate.historicalDebt AS historicalDebt,
               loaneeLoanAggregate.totalAmountOutstanding AS totalAmountOutstanding,
               loaneeLoanAggregate.numberOfLoans AS numberOfLoans,
               loanee.userIdentity.firstName AS firstName,
               loanee.userIdentity.lastName AS lastName,
               loanee.userIdentity.email AS email,
               loanee.id AS loaneeId
        FROM LoaneeLoanAggregateEntity loaneeLoanAggregate
        JOIN LoaneeEntity loanee ON loanee.id = loaneeLoanAggregate.loanee.id
        JOIN CohortLoaneeEntity cohortLoanee ON cohortLoanee.loanee.id = loanee.id
        JOIN CohortEntity cohort ON cohort.id = cohortLoanee.cohort.id
        JOIN ProgramEntity program ON program.id = cohort.programId
        JOIN OrganizationEntity organization ON organization.id = program.organizationIdentity.id
        WHERE (loanee.userIdentity.firstName LIKE :nameFragment
           OR loanee.userIdentity.lastName LIKE :nameFragment)
           AND organization.id = :organizationId
        ORDER BY loaneeLoanAggregate.numberOfLoans DESC
    """)
    Page<LoaneeLoanAggregateProjection> searchLoaneeLoanAggregateByOrganizationId(@Param("nameFragment") String nameFragment,
                                                                                  @Param("organizationId") String organizationId, Pageable pageRequest);
}
