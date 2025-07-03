package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

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

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoanCalculationServiceTest {
    @InjectMocks
    private LoanCalculationService loanCalculation;
    @BeforeEach
    void setup() {
    }

    private RepaymentHistory createRepayment(LocalDateTime time, BigDecimal amount) {
        return RepaymentHistory.builder()
                .paymentDateTime(time)
                .amountPaid(amount)
                .build();
    }

    @Test
    void testSortRepayments_validList_sortedDescending() throws MeedlException {
        List<RepaymentHistory> repayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 6, 1, 10, 0), new BigDecimal("100")),
                createRepayment(LocalDateTime.of(2025, 6, 3, 9, 0), new BigDecimal("300")),
                createRepayment(LocalDateTime.of(2025, 6, 2, 12, 0), new BigDecimal("200"))
        ));

        List<RepaymentHistory> sorted = loanCalculation.sortRepaymentsByDateDescending(repayments);

        assertEquals(new BigDecimal("300"), sorted.get(0).getAmountPaid());
        assertEquals(new BigDecimal("200"), sorted.get(1).getAmountPaid());
        assertEquals(new BigDecimal("100"), sorted.get(2).getAmountPaid());
    }

    @Test
    void testSortRepayments_nullList_returnsEmptyList() throws MeedlException {
        List<RepaymentHistory> result = loanCalculation.sortRepaymentsByDateDescending(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSortRepayments_containsNullRepayment_throwsException() {
        List<RepaymentHistory> repayments = new ArrayList<>();
        repayments.add(createRepayment(LocalDateTime.now(), new BigDecimal("100")));
        repayments.add(null);

        MeedlException exception = assertThrows(MeedlException.class,
                () -> loanCalculation.sortRepaymentsByDateDescending(repayments));

        assertEquals("Repayment history cannot be null", exception.getMessage());
    }

    @Test
    void testSortRepayments_containsNullDate_throwsException() {
        RepaymentHistory badRepayment = createRepayment(null, new BigDecimal("150"));
        List<RepaymentHistory> repayments = List.of(badRepayment);

        MeedlException exception = assertThrows(MeedlException.class,
                () -> loanCalculation.sortRepaymentsByDateDescending(repayments));

        assertEquals("Payment date cannot be null", exception.getMessage());
    }
}
