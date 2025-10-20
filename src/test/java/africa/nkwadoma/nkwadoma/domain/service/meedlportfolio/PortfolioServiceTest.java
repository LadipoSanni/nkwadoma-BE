package africa.nkwadoma.nkwadoma.domain.service.meedlportfolio;


import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PlatformRequestOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.PortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanMetricsProjection;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;
    @Mock
    private PortfolioOutputPort portfolioOutputPort;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private PortfolioMapper portfolioMapper;


    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;

    @Mock
    private PlatformRequestOutputPort platformRequestOutputPort;

    @Mock
    private AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private Portfolio portfolio;
    private UserIdentity actor;


    @BeforeEach
    void setUp() {
        portfolio = TestData.createMeedlPortfolio();
        actor = TestData.createTestUserIdentity(TestUtils.generateEmail(8));
    }

    @Test
    void viewPortfolio() {
        try {
            when(portfolioOutputPort.findPortfolio(Mockito.any(Portfolio.class)))
                    .thenReturn(portfolio);
            LoanMetricsProjection loanMetricsProjection = Mockito.mock(LoanMetricsProjection.class);
            when(loanMetricsOutputPort.calculateAllMetrics()).thenReturn(loanMetricsProjection);
            portfolio = portfolioService.viewPortfolio();
        }catch (MeedlException meedlException) {
            log.info(meedlException.getMessage());
        }
        assertNotNull(portfolio);
    }

    @Test
    void setNullObligorLoanLimit() {
        assertThrows(MeedlException.class, () -> portfolioService.setUpMeedlObligorLoanLimit(null));
    }

    @Test
    void setInvalidObligorLoanLimit() throws MeedlException {
        Portfolio invalidPortfolio = Mockito.mock(Portfolio.class);
        doThrow(new MeedlException("Validation failed"))
                .when(invalidPortfolio).validateObligorLimitDetail();
        portfolioService.setUpMeedlObligorLoanLimit(invalidPortfolio);
        assertThrows(MeedlException.class, () -> portfolioService.setUpMeedlObligorLoanLimit(invalidPortfolio));
    }

    @Test
    void shouldUpdatePortfolioWhenActorIsSuperAdmin() throws MeedlException {
        actor.setRole(IdentityRole.MEEDL_SUPER_ADMIN);
        portfolio.setUserIdentity(actor);

        when(userIdentityOutputPort.findById(actor.getId())).thenReturn(actor);
        when(portfolioOutputPort.findPortfolio(portfolio)).thenReturn(portfolio);

        portfolioService.setUpMeedlObligorLoanLimit(portfolio);

        verify(portfolioOutputPort).save(portfolio);
        verify(platformRequestOutputPort, never()).save(any());
        verify(asynchronousNotificationOutputPort, never())
                .notifySuperAdminOfMeedlObligorLoanLimitChange(any(), any());
    }

    @Test
    void shouldCreatePlatformRequestWhenActorIsNotSuperAdmin() throws MeedlException {
        actor.setRole(IdentityRole.MEEDL_ADMIN);
        portfolio.setUserIdentity(actor);

        when(userIdentityOutputPort.findById(actor.getId())).thenReturn(actor);

        PlatformRequest request = PlatformRequest.builder()
                .id("req-123")
                .obligorLoanLimit(BigDecimal.TEN)
                .createdBy(actor.getId())
                .requestTime(LocalDateTime.now())
                .build();

        when(platformRequestOutputPort.save(any(PlatformRequest.class))).thenReturn(request);

        portfolioService.setUpMeedlObligorLoanLimit(portfolio);

        verify(platformRequestOutputPort).save(any(PlatformRequest.class));
        verify(asynchronousNotificationOutputPort)
                .notifySuperAdminOfMeedlObligorLoanLimitChange(eq(actor), any(PlatformRequest.class));
        verify(portfolioOutputPort, never()).save(any());
    }
}
