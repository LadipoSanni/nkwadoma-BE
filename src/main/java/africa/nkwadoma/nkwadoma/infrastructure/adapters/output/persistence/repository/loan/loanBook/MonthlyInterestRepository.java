package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.MonthlyInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.Month;

public interface MonthlyInterestRepository extends JpaRepository<MonthlyInterestEntity, String> {


    @Query("""
    select monthlyInterest from MonthlyInterestEntity monthlyInterest
        where monthlyInterest.loaneeLoanDetail.id = :loaneeLoanDetailId
            and extract(MONTH from monthlyInterest.createdAt) = :createdAt_month
            and extract(YEAR from monthlyInterest.createdAt) = :createdAt_year
    """)
    MonthlyInterestEntity findByLoaneeLoanDetailIdAndCreatedAtMonthAndCreatedAtYear(
            @Param("loaneeLoanDetailId")String loaneeLoanDetailId,
            @Param("createdAt_month") int createdAt_month,
            @Param("createdAt_year") int createdAt_year);
}
