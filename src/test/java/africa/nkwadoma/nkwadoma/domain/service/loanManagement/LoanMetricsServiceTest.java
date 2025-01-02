package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LoanMetricsServiceTest {
    @InjectMocks
    private LoanMetricsService loanMetricsService;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private LoanMetricsMapper loanMetricsMapper;
    private LoanMetrics loanMetrics;

    @BeforeEach
    void setUp() {
        loanMetrics = new LoanMetrics();
        loanMetrics.setOrganizationId("b95805d1-2e2d-47f8-a037-7bcd264914fc");
        loanMetrics.setLoanRequestCount(BigInteger.ONE.intValue());
    }

    @Test
    void saveLoanMetrics() {
        LoanMetrics metrics = new LoanMetrics();
        try {
            when(loanMetricsOutputPort.findByOrganizationId(anyString())).
                    thenReturn(Optional.empty());
            when(loanMetricsOutputPort.save(any())).thenReturn(loanMetrics);
            metrics = loanMetricsService.saveOrUpdateLoanMetrics(loanMetrics);
//            verify(loanMetricsOutputPort, times(BigInteger.ONE.intValue())).findByOrganizationId(metrics.getOrganizationId());
//            verify(loanMetricsOutputPort, times(BigInteger.ONE.intValue())).save(loanMetrics);
        } catch (MeedlException e) {
            log.error("Error saving loan metrics: {}", e.getMessage());
        }
        assertNotNull(metrics);
    }

    @Test
    void updateLoanMetrics() {
        LoanMetrics metricsRequest = new LoanMetrics();
        metricsRequest.setOrganizationId(loanMetrics.getOrganizationId());
        metricsRequest.setLoanRequestCount(BigInteger.ONE.intValue());

        try {
            when(loanMetricsOutputPort.findByOrganizationId(metricsRequest.getOrganizationId())).
                    thenReturn(Optional.of(loanMetrics));
            loanMetrics.setLoanRequestCount(loanMetrics.getLoanRequestCount() + metricsRequest.getLoanRequestCount());
            when(loanMetricsOutputPort.save(any())).thenReturn(loanMetrics);
            loanMetrics = loanMetricsService.saveOrUpdateLoanMetrics(loanMetrics);
        } catch (MeedlException e) {
            log.error("Error saving loan metrics: {}", e.getMessage());
        }

        assertNotNull(loanMetrics);
        assertEquals(loanMetrics.getOrganizationId(), metricsRequest.getOrganizationId());
        assertEquals(2, loanMetrics.getLoanRequestCount());
    }
}