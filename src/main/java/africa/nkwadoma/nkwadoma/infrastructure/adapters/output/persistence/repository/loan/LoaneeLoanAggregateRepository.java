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
        COUNT(l) AS numberOfLoanee
    FROM LoaneeLoanAggregateEntity l
""")
    LoanSummaryProjection getLoanSummary();
}
