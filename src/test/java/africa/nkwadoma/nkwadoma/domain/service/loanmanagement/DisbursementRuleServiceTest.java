package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DisbursementRuleServiceTest {

    @InjectMocks
    private DisbursementRuleService disbursementRuleService;

    @Mock
    private DisbursementRuleOutputPort disbursementRuleOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;

    private DisbursementRule disbursementRule;
    private UserIdentity superAdminUser;
    private UserIdentity normalUser;

    @BeforeEach
    void setUp() {
        superAdminUser = new UserIdentity();
        superAdminUser.setId(UUID.randomUUID().toString());
        superAdminUser.setRole(IdentityRole.MEEDL_SUPER_ADMIN); // assuming you have an enum/role like this

        normalUser = new UserIdentity();
        normalUser.setId(UUID.randomUUID().toString());
        normalUser.setRole(IdentityRole.PORTFOLIO_MANAGER);

        disbursementRule = TestData.buildDisbursementRule();
        disbursementRule.setName("Test Rule");

        disbursementRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
    }

    @Test
    void createDisbursementRuleWithExistingName() throws MeedlException {
        disbursementRule.setUserIdentity(superAdminUser);

        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(MeedlException.class, () -> disbursementRuleService.createDisbursementRule(disbursementRule));

        verify(disbursementRuleOutputPort, never()).save(any());
    }

    @Test
    void superAdminCreateAutomaticallyApprovedDisbursementRule() throws MeedlException {
        disbursementRule.setUserIdentity(superAdminUser);

        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(false);
        when(userIdentityOutputPort.findById(anyString())).thenReturn(superAdminUser);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.createDisbursementRule(disbursementRule);

        assertThat(result.getActivationStatus()).isEqualTo(ActivationStatus.APPROVED);
        verify(asynchronousNotificationOutputPort, never()).notifyAdminOfDisbursementRuleApproval(any());
    }

    @Test
    void createDisbursementRuleWithNoneSuperAdminRequireApproval() throws MeedlException {
        disbursementRule.setUserIdentity(normalUser);
        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(false);
        when(userIdentityOutputPort.findById(anyString())).thenReturn(normalUser);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.createDisbursementRule(disbursementRule);

        assertThat(result.getActivationStatus()).isEqualTo(ActivationStatus.PENDING_APPROVAL);
        verify(asynchronousNotificationOutputPort).notifyAdminOfDisbursementRuleApproval(any());
    }

    @Test
    void createInactiveDisbursementRule() throws MeedlException {
        disbursementRule.setUserIdentity(superAdminUser);
        disbursementRule.setActivationStatus(ActivationStatus.INVITED);

        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(false);
        when(userIdentityOutputPort.findById(anyString())).thenReturn(normalUser);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.createDisbursementRule(disbursementRule);

        assertThat(result.getActivationStatus()).isEqualTo(ActivationStatus.INACTIVE);
    }

    @Test
    void viewDisbursementRuleWithValidId() throws MeedlException {
        disbursementRule.setId(UUID.randomUUID().toString());
        when(disbursementRuleOutputPort.findById(anyString())).thenReturn(disbursementRule);

        DisbursementRule result = disbursementRuleService.viewDisbursementRule(disbursementRule);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Rule");
    }

    @Test
    void viewAllDisbursementRule() throws MeedlException {
        Page<DisbursementRule> page = new PageImpl<>(List.of(disbursementRule));
        when(disbursementRuleOutputPort.findAllDisbursementRule(any())).thenReturn(page);

        Page<DisbursementRule> result = disbursementRuleService.viewAllDisbursementRule(disbursementRule);

        assertThat(result.getContent()).hasSize(1);
    }
    @Test
    void  viewDisbursementRuleWithInvalidId(){
        disbursementRule.setId("");
        assertThrows(MeedlException.class, ()-> disbursementRuleService.viewDisbursementRule(disbursementRule));
    }
    @Test
    void respondToDisbursementRuleWithInvalidActivationStatus() throws MeedlException {
        disbursementRule.setUserIdentity(normalUser);
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL); // not APPROVED or DECLINED

        assertThrows(MeedlException.class,
                () -> disbursementRuleService.respondToDisbursementRule(disbursementRule));

        verify(disbursementRuleOutputPort, never()).save(any());
    }

    @Test
    void respondToDisbursementRuleApprove() throws MeedlException {
        disbursementRule.setUserIdentity(normalUser);
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.APPROVED);

        when(disbursementRuleOutputPort.findById(disbursementRule.getId())).thenReturn(disbursementRule);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.respondToDisbursementRule(disbursementRule);

        assertNotNull(result);
        assertEquals(ActivationStatus.APPROVED, result.getActivationStatus());
        verify(disbursementRuleOutputPort).save(disbursementRule);
    }

    @Test
    void respondToDisbursementRuleDecline() throws MeedlException {
        disbursementRule.setUserIdentity(normalUser);
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.DECLINED);

        when(disbursementRuleOutputPort.findById(disbursementRule.getId())).thenReturn(disbursementRule);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.respondToDisbursementRule(disbursementRule);

        assertNotNull(result);
        assertEquals(ActivationStatus.DECLINED, result.getActivationStatus());
        verify(disbursementRuleOutputPort).save(disbursementRule);
    }

    @Test
    void respondToDisbursementRuleWithNullRule() throws MeedlException {
        assertThrows(MeedlException.class,
                () -> disbursementRuleService.respondToDisbursementRule(null));
        verify(disbursementRuleOutputPort, never()).save(any());
    }

    @Test
    void deleteDisbursementRuleById() throws MeedlException {
        disbursementRule.setId(UUID.randomUUID().toString());

        disbursementRuleService.deleteDisbursementRuleById(disbursementRule);

        verify(disbursementRuleOutputPort).deleteById(disbursementRule.getId());
    }
}

