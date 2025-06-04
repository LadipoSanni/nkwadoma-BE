package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistoryEntity,String> {
    @Query("SELECT r FROM RepaymentHistoryEntity r WHERE " +
            "(:loaneeId IS NULL OR r.loanee.id = :loaneeId) AND " +
            "(:month IS NULL OR MONTH(r.paymentDateTime) = :month) AND " +
            "(:year IS NULL OR YEAR(r.paymentDateTime) = :year)")
    Page<RepaymentHistoryEntity> findRepaymentHistoryByLoaneeIdOrAll(@Param("loaneeId") String loaneeId,
                                                                     @Param("month") Integer month,
                                                                     @Param("year") Integer year,Pageable pageable);


    @Query("SELECT r FROM RepaymentHistoryEntity r " +
            "JOIN r.loanee l " +
            "JOIN l.userIdentity u " +
            "WHERE (:month IS NULL OR MONTH(r.paymentDateTime) = :month) AND " +
            "(:year IS NULL OR YEAR(r.paymentDateTime) = :year) AND " +
            "(:name IS NULL OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<RepaymentHistoryEntity> searchRepaymentHistory(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("name") String name,
            Pageable pageable);

}

