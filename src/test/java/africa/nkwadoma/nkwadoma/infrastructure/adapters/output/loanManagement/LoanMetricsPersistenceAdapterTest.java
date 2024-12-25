package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class LoanMetricsPersistenceAdapterTest {
    @Autowired
    private LoanMetricsOutputPort loanMetricsOutputPort;
    private LoanMetrics loanMetrics;
    private LoanMetrics secondLoanMetrics;
    private String loanMetricsId;
    private String secondLoanMetricsId;

    @BeforeEach
    void setUp() {
        loanMetrics = LoanMetrics.builder()
                .organizationId("ead0f7cb-5483-4bb8-b271-813970a9c368")
                .loanRequestCount(2)
                .build();
        secondLoanMetrics = LoanMetrics.builder()
                .organizationId("1886df42-1f75-4d17-bdef-e0b016707885")
                .loanRequestCount(1)
                .build();
    }

    @AfterEach
    void tearDown() {
        if (StringUtils.isNotEmpty(loanMetricsId) ||
                StringUtils.isNotEmpty(secondLoanMetricsId)) {
            try {
                loanMetricsOutputPort.delete(loanMetricsId);
                loanMetricsOutputPort.delete(secondLoanMetricsId);
            } catch (MeedlException e) {
                log.error("Exception occurred deleting loan metrics {}", e.getMessage());
            }
        }
//        if (StringUtils.isNotEmpty(secondLoanMetricsId)) {
//            try {
//                loanMetricsOutputPort.delete(secondLoanMetricsId);
//            } catch (MeedlException e) {
//                log.error("Exception occurred deleting loan metrics {}", e.getMessage());
//            }
//        }
    }

    @Test
    void saveLoanMetrics() {
        LoanMetrics savedLoanMetrics = null;
        try {
            savedLoanMetrics = loanMetricsOutputPort.save(loanMetrics);
        } catch (MeedlException e) {
            log.error("Exception occurred saving loan metrics {}", e.getMessage());
        }
        assertNotNull(savedLoanMetrics);
        assertNotNull(savedLoanMetrics.getId());
        loanMetricsId = savedLoanMetrics.getId();
    }

    @Test
    void findTopOrganizationWithLoanRequest() {
        LoanMetrics savedLoanMetrics = null;
        LoanMetrics secondSavedLoanMetrics = null;
        try {
            savedLoanMetrics = loanMetricsOutputPort.save(loanMetrics);
            secondSavedLoanMetrics = loanMetricsOutputPort.save(secondLoanMetrics);
        } catch (MeedlException e) {
            log.error("Exception occurred saving loan metrics {}", e.getMessage());
        }
        assertNotNull(savedLoanMetrics);
        assertNotNull(savedLoanMetrics.getId());
        loanMetricsId = savedLoanMetrics.getId();
        assertNotNull(secondSavedLoanMetrics);
        secondLoanMetricsId = secondSavedLoanMetrics.getId();

        Optional<LoanMetrics> foundLoanMetrics = loanMetricsOutputPort.findTopOrganizationWithLoanRequest();

        assertFalse(foundLoanMetrics.isEmpty());
        assertEquals(foundLoanMetrics.get().getOrganizationId(), savedLoanMetrics.getOrganizationId());
        assertEquals(2, foundLoanMetrics.get().getLoanRequestCount());
    }
}