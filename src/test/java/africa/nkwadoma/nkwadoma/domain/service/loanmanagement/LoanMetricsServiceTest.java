package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.*;
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
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
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

        LoanMetrics expectedMetrics = new LoanMetrics();
        expectedMetrics.setOrganizationId(loanMetrics.getOrganizationId());
        expectedMetrics.setLoanRequestCount(2);

        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setId(loanMetrics.getOrganizationId());
        try {
            when(organizationIdentityOutputPort.findById(anyString())).thenReturn(organizationIdentity);
            when(loanMetricsOutputPort.findByOrganizationId(anyString())).
                    thenReturn(Optional.empty());
            when(loanMetricsOutputPort.save(any())).thenReturn(loanMetrics);
            metrics = loanMetricsService.save(loanMetrics);
            verify(loanMetricsOutputPort, times(BigInteger.ONE.intValue())).findByOrganizationId(any());
            verify(loanMetricsOutputPort, times(BigInteger.ONE.intValue())).save(loanMetrics);
        } catch (MeedlException e) {
            log.error("Error saving loan metrics: {}", e.getMessage());
        }
        assertNotNull(metrics);
        assertEquals(metrics.getOrganizationId(), loanMetrics.getOrganizationId());
    }

    @Test
    void updateLoanMetrics() {
        LoanMetrics metricsRequest = new LoanMetrics();
        metricsRequest.setOrganizationId(loanMetrics.getOrganizationId());
        metricsRequest.setLoanRequestCount(BigInteger.ONE.intValue());

        LoanMetrics expectedMetrics = new LoanMetrics();
        expectedMetrics.setOrganizationId(loanMetrics.getOrganizationId());
        expectedMetrics.setLoanRequestCount(BigInteger.TWO.intValue());

        try {
            when(loanMetricsOutputPort.findByOrganizationId(metricsRequest.getOrganizationId())).
                    thenReturn(Optional.of(loanMetrics));
            when(loanMetricsOutputPort.save(any())).thenReturn(expectedMetrics);
            loanMetrics = loanMetricsService.save(loanMetrics);

            verify(loanMetricsOutputPort, times(BigInteger.ONE.intValue())).
                    findByOrganizationId(metricsRequest.getOrganizationId());
            verify(loanMetricsOutputPort, times(1)).save(any(LoanMetrics.class));
        } catch (MeedlException e) {
            log.error("Error saving loan metrics: {}", e.getMessage());
        }

        assertNotNull(loanMetrics);
        assertEquals(loanMetrics.getOrganizationId(), metricsRequest.getOrganizationId());
        assertEquals(BigInteger.TWO.intValue(), loanMetrics.getLoanRequestCount());
    }

    @Test
    void saveLoanMetricsWithNonExistingOrganizationId() {
        LoanMetrics metrics = new LoanMetrics();
        metrics.setOrganizationId("505284c5-b015-4fce-ba45-ab701c1d2965");
        metrics.setLoanRequestCount(2);
        try {
            doThrow(new MeedlException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage()))
                    .when(organizationIdentityOutputPort).findById(anyString());
        } catch (MeedlException e) {
            log.error("Exception occurred: {}", e.getMessage());
        }
        assertThrows(MeedlException.class, ()->loanMetricsService.save(metrics));
    }
}