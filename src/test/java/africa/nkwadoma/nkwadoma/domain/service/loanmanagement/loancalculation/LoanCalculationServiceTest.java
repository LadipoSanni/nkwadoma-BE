package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanCalculationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoanCalculationServiceTest {
    @InjectMocks
    private LoanCalculationService loanCalculation;
    @Mock
    private RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private String loaneeId;
    private String cohortId;
    @BeforeEach
    void setup() {
        loaneeId = UUID.randomUUID().toString();
        cohortId = UUID.randomUUID().toString();
    }

    private RepaymentHistory createRepayment(LocalDateTime time, BigDecimal amount) {
        return RepaymentHistory.builder()
                .paymentDateTime(time)
                .amountPaid(amount)
                .build();
    }

    @Test
    void sortRepaymentsWithValidRecordsSortedInDescendingOrder() throws MeedlException {
        List<RepaymentHistory> repayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 6, 1, 10, 0), new BigDecimal("100")),
                createRepayment(LocalDateTime.of(2025, 6, 3, 9, 0), new BigDecimal("300")),
                createRepayment(LocalDateTime.of(2025, 6, 2, 12, 0), new BigDecimal("200"))
        ));

        List<RepaymentHistory> sorted = loanCalculation.sortRepaymentsByDateTimeAscending(repayments);

        assertEquals(new BigDecimal("300"), sorted.get(2).getAmountPaid());
        assertEquals(new BigDecimal("200"), sorted.get(1).getAmountPaid());
        assertEquals(new BigDecimal("100"), sorted.get(0).getAmountPaid());
    }

    @Test
    void sortRepaymentsWithNull() throws MeedlException {
        List<RepaymentHistory> result = loanCalculation.sortRepaymentsByDateTimeAscending(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void sortRepaymentsWithNullRepayment() {
        List<RepaymentHistory> repayments = new ArrayList<>();
        repayments.add(createRepayment(LocalDateTime.now(), new BigDecimal("100")));
        repayments.add(null);

        MeedlException exception = assertThrows(MeedlException.class,
                () -> loanCalculation.sortRepaymentsByDateTimeAscending(repayments));

        assertEquals(LoanCalculationMessages.REPAYMENT_HISTORY_MUST_BE_PROVIDED.getMessage(), exception.getMessage());
    }

    @Test
    void sortRepaymentsWithNullDate() {
        RepaymentHistory badRepayment = createRepayment(null, new BigDecimal("150"));
        List<RepaymentHistory> repayments = List.of(badRepayment);

        MeedlException exception = assertThrows(MeedlException.class,
                () -> loanCalculation.sortRepaymentsByDateTimeAscending(repayments));

        assertEquals(LoanCalculationMessages.PAYMENT_DATE_CANNOT_BE_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void accumulateTotalRepaidWIthNoPreviousRepayment() throws MeedlException {
        List<RepaymentHistory> repayments = List.of(
                createRepayment(LocalDateTime.of(2025, 1, 1, 10, 0), new BigDecimal("1000")),
                createRepayment(LocalDateTime.of(2025, 2, 1, 10, 0), new BigDecimal("2000")),
                createRepayment(LocalDateTime.of(2025, 3, 1, 10, 0), new BigDecimal("5000"))
        );

        BigDecimal totalAmountRepaid = loanCalculation.calculateCurrentTotalAmountRepaid(repayments, loaneeId, cohortId);
        log.info("Updated repayment history in test after both sorting \n {}", totalAmountRepaid);
        assertEquals(new BigDecimal("1000"), repayments.get(0).getTotalAmountRepaid());
        assertEquals(new BigDecimal("3000"), repayments.get(1).getTotalAmountRepaid());
        assertEquals(new BigDecimal("8000"), repayments.get(2).getTotalAmountRepaid());
        assertEquals(new BigDecimal("8000"), totalAmountRepaid);
    }

    @Test
    void accumulateTotalRepaidWithPreviousRepayments() throws MeedlException {
        List<RepaymentHistory> previousRepayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 1, 1, 10, 0), new BigDecimal("1000")),
                createRepayment(LocalDateTime.of(2025, 2, 1, 10, 0), new BigDecimal("2000"))
        ));

        List<RepaymentHistory> newRepayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 3, 1, 10, 0), new BigDecimal("3000")),
                createRepayment(LocalDateTime.of(2025, 4, 1, 10, 0), new BigDecimal("4000"))
        ));

        when(repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loaneeId, cohortId))
                .thenReturn(previousRepayments);

        BigDecimal totalAmountRepaid = loanCalculation.calculateCurrentTotalAmountRepaid(newRepayments, loaneeId, cohortId);


        assertEquals(new BigDecimal("10000"), totalAmountRepaid);
    }


}
