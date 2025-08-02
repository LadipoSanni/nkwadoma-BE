package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DailyInterestEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
