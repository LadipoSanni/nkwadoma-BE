package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistoryEntity,String> {

    @Query("""
            SELECT
                u.firstName as firstName,
                u.lastName as lastName,
                r.paymentDateTime as paymentDateTime,

                r.amountPaid as amountPaid,
                r.totalAmountRepaid as totalAmountRepaid,
                r.amountOutstanding as amountOutstanding,
                r.modeOfPayment as modeOfPayment,
                r.id as id,
                (CASE WHEN :loaneeId IS NULL THEN
                    (SELECT MIN(YEAR(r2.paymentDateTime)) FROM RepaymentHistoryEntity r2)
                 ELSE
                    (SELECT MIN(YEAR(r2.paymentDateTime)) FROM RepaymentHistoryEntity r2 WHERE r2.loanee.id = l.id)
                 END) as firstYear,
                (CASE WHEN :loaneeId IS NULL THEN
                    (SELECT MAX(YEAR(r2.paymentDateTime)) FROM RepaymentHistoryEntity r2)
                 ELSE
                    (SELECT MAX(YEAR(r2.paymentDateTime)) FROM RepaymentHistoryEntity r2 WHERE r2.loanee.id = l.id)
                 END) as lastYear
            FROM RepaymentHistoryEntity r
            JOIN r.loanee l
            JOIN l.userIdentity u
            WHERE (:loaneeId IS NULL OR l.id = :loaneeId) AND
                  (:month IS NULL OR MONTH(r.paymentDateTime) = :month) AND
                  (:year IS NULL OR YEAR(r.paymentDateTime) = :year)
            """)
    Page<RepaymentHistoryProjection> findRepaymentHistoryByLoaneeIdOrAll(
            @Param("loaneeId") String loaneeId,
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable);

    @Query("""
        SELECT
            COALESCE((
                SELECT CAST(EXTRACT(YEAR FROM r.paymentDateTime) AS INTEGER)
                FROM RepaymentHistoryEntity r
                WHERE (:loaneeId IS NULL OR r.loanee.id = :loaneeId)
                ORDER BY r.paymentDateTime ASC
                LIMIT 1
            ), 0) as firstYear,
            COALESCE((
                SELECT CAST(EXTRACT(YEAR FROM r.paymentDateTime) AS INTEGER)
                FROM RepaymentHistoryEntity r
                WHERE (:loaneeId IS NULL OR r.loanee.id = :loaneeId)
                ORDER BY r.paymentDateTime DESC
                LIMIT 1
            ), 0) as lastYear
    """)
    Map<String, Integer> getFirstAndLastYear(@Param("loaneeId") String loaneeId);

    @Query("""
            SELECT
                u.firstName as firstName,
                u.lastName as lastName,
                r.paymentDateTime as paymentDateTime,
                
                r.amountPaid as amountPaid,
                r.totalAmountRepaid as totalAmountRepaid,
                r.amountOutstanding as amountOutstanding,
                r.modeOfPayment as modeOfPayment,
                r.id as id,
                (SELECT MIN(YEAR(r2.paymentDateTime)) FROM RepaymentHistoryEntity r2 WHERE r2.loanee.id = l.id) as firstYear,
                (SELECT MAX(YEAR(r2.paymentDateTime)) FROM RepaymentHistoryEntity r2 WHERE r2.loanee.id = l.id) as lastYear
            FROM RepaymentHistoryEntity r
            JOIN r.loanee l
            JOIN l.userIdentity u
            WHERE (:month IS NULL OR MONTH(r.paymentDateTime) = :month) AND
                  (:year IS NULL OR YEAR(r.paymentDateTime) = :year) AND
                  (:name IS NULL OR
                   LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                   LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
            """)
    Page<RepaymentHistoryProjection> searchRepaymentHistory(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("name") String name,
            Pageable pageable);
    @Query("""
    SELECT r FROM RepaymentHistoryEntity r 
    WHERE r.loanee.id = :loaneeId AND r.cohortId = :cohortId 
    ORDER BY r.paymentDateTime DESC
    LIMIT 1
""")
    Optional<RepaymentHistoryEntity> findTopByLoaneeIdAndCohortIdOrderByPaymentDateTimeDesc(
            @Param("loaneeId") String loaneeId,
            @Param("cohortId") String cohortId
    );

    List<RepaymentHistoryEntity> findAllByLoanee_IdAndCohortIdOrderByPaymentDateTimeAsc(String loaneeId, String cohortId);

    @Modifying
    @Query("DELETE FROM RepaymentHistoryEntity r WHERE r.id IN :ids")
    void deleteByIds(@Param("ids") List<String> ids);


}

