package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DailyInterestEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public interface DailyInterestRepository extends JpaRepository<DailyInterestEntity,String> {


    @Query("""
            SELECT d FROM DailyInterestEntity d WHERE d.loaneeLoanDetail.id = :loaneeLoanDetailId
            AND EXTRACT(MONTH FROM d.createdAt) = :month
            AND EXTRACT(YEAR FROM d.createdAt) = :year
            """)
    List<DailyInterestEntity> findAllByLoaneeLoanDetailIdAndCreatedAtMonthAndCreatedAtYear(
            @Param("loaneeLoanDetailId") String loaneeLoanDetailId,
            @Param("month") int month,
            @Param("year") int year);


    @Query("""
    select dailyInterest from DailyInterestEntity dailyInterest
        where dailyInterest.loaneeLoanDetail.id = :loaneeLoanDetailId
            and EXTRACT(MONTH FROM dailyInterest.createdAt) = :createdAt_month
            and EXTRACT(YEAR FROM dailyInterest.createdAt) = :createdAt_year
            and EXTRACT(DAY FROM dailyInterest.createdAt) = :createdAt_dayOfMonth
    """)
    DailyInterestEntity findByCreatedAt_DayOfMonthAndCreatedAtMonthAndCreatedAtYear(
            @Param("createdAt_dayOfMonth") int createdAt_dayOfMonth,
            @Param("createdAt_month") int createdAt_month,
            @Param("createdAt_year") int createdAt_year,
            @Param("loaneeLoanDetailId")String loaneeLoanDetailId);
}
