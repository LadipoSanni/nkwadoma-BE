package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement.DisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private DisbursementRuleMapper disbursementRuleMapper;
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
    void editDisbursementRuleWhenNotApproved() throws MeedlException {
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        disbursementRule.setPercentageDistribution(List.of(50.0, 50.0)); // must sum to 100

        DisbursementRule existingRule = TestData.buildDisbursementRule();
        existingRule.setId(disbursementRule.getId());
        existingRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        existingRule.setName("Old Name");
        existingRule.setPercentageDistribution(List.of(60.0, 40.0)); // valid too

        when(disbursementRuleOutputPort.findById(disbursementRule.getId()))
                .thenReturn(existingRule);
        when(disbursementRuleOutputPort.save(any(DisbursementRule.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        existingRule.setName(disbursementRule.getName());
        doNothing().when(disbursementRuleMapper)
                .edit(existingRule, disbursementRule);
        DisbursementRule updated = disbursementRuleService.editDisbursementRule(disbursementRule);

        assertEquals(disbursementRule.getName(), updated.getName());
        verify(disbursementRuleOutputPort).save(existingRule);
    }
    @Test
    void editDisbursementRuleWhenApproved() throws MeedlException {
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.APPROVED);
        disbursementRule.setPercentageDistribution(List.of(50.0, 50.0));

        DisbursementRule existingRule = TestData.buildDisbursementRule();
        existingRule.setId(disbursementRule.getId());
        existingRule.setActivationStatus(ActivationStatus.APPROVED);
        existingRule.setName("Old Name");
        existingRule.setPercentageDistribution(List.of(60.0, 40.0));

        when(disbursementRuleOutputPort.findById(disbursementRule.getId()))
                .thenReturn(existingRule);

        DisbursementRule result = disbursementRuleService.editDisbursementRule(disbursementRule);

        // should just return the found rule, no update
        assertEquals(existingRule, result);
        verify(disbursementRuleOutputPort, never()).save(any());
    }
    @Test
    void editDisbursementRuleWithInvalidDistributionThrows() {
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        disbursementRule.setPercentageDistribution(List.of(40.0, 40.0)); // sum != 100

        assertThrows(MeedlException.class, () ->
                disbursementRuleService.editDisbursementRule(disbursementRule)
        );
    }
    @Test
    void editDisbursementRuleWhenNameAlreadyExistsThrows() throws MeedlException {
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        disbursementRule.setName("New Rule");
        disbursementRule.setPercentageDistribution(List.of(70.0, 30.0));

        DisbursementRule existingRule = TestData.buildDisbursementRule();
        existingRule.setId(disbursementRule.getId());
        existingRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        existingRule.setName("Old Rule");
        existingRule.setPercentageDistribution(List.of(60.0, 40.0));

        when(disbursementRuleOutputPort.findById(disbursementRule.getId()))
                .thenReturn(existingRule);
        when(disbursementRuleOutputPort.existByNameIgnoreCase("New Rule"))
                .thenReturn(true);

        assertThrows(MeedlException.class, () ->
                disbursementRuleService.editDisbursementRule(disbursementRule)
        );
    }


    @Test
    void attemptToUpdateApprovedDisbursementRule() throws MeedlException {
        disbursementRule.setId(UUID.randomUUID().toString());
        disbursementRule.setActivationStatus(ActivationStatus.APPROVED);

        DisbursementRule existingRule = TestData.buildDisbursementRule();
        existingRule.setId(disbursementRule.getId());
        existingRule.setActivationStatus(ActivationStatus.APPROVED);
        existingRule.setName("Approved Rule");

        when(disbursementRuleOutputPort.findById(disbursementRule.getId()))
                .thenReturn(existingRule);

        DisbursementRule result = disbursementRuleService.editDisbursementRule(disbursementRule);

        assertEquals("Approved Rule", result.getName());
        verify(disbursementRuleOutputPort, never()).save(any());
    }


    @Test
    void setUpDisbursementRuleWithExistingName() throws MeedlException {
        disbursementRule.setUserIdentity(superAdminUser);

        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(MeedlException.class, () -> disbursementRuleService.setUpDisbursementRule(disbursementRule));

        verify(disbursementRuleOutputPort, never()).save(any());
    }

    @Test
    void superAdminCreateAutomaticallyApprovedDisbursementRule() throws MeedlException {
        disbursementRule.setUserIdentity(superAdminUser);

        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(false);
        when(userIdentityOutputPort.findById(anyString())).thenReturn(superAdminUser);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.setUpDisbursementRule(disbursementRule);

        assertThat(result.getActivationStatus()).isEqualTo(ActivationStatus.APPROVED);
        verify(asynchronousNotificationOutputPort, never()).notifyAdminOfDisbursementRuleApproval(any());
    }

    @Test
    void setUpDisbursementRuleWithNoneSuperAdminRequireApproval() throws MeedlException {
        disbursementRule.setUserIdentity(normalUser);
        when(disbursementRuleOutputPort.existByNameIgnoreCase(anyString())).thenReturn(false);
        when(userIdentityOutputPort.findById(anyString())).thenReturn(normalUser);
        when(disbursementRuleOutputPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisbursementRule result = disbursementRuleService.setUpDisbursementRule(disbursementRule);

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

        DisbursementRule result = disbursementRuleService.setUpDisbursementRule(disbursementRule);

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
        disbursementRule.setActivationStatus(ActivationStatus.PENDING_APPROVAL);

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

    // =============================
    // NEW SEARCH TESTS
    // =============================

    @Test
    void searchWithValidDisbursementRule() throws MeedlException {
        // given
        Page<DisbursementRule> expectedPage = new PageImpl<>(List.of(disbursementRule));
        when(disbursementRuleOutputPort.search(any(DisbursementRule.class)))
                .thenReturn(expectedPage);

        // when
        Page<DisbursementRule> result = disbursementRuleService.search(disbursementRule);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Test Rule", result.getContent().get(0).getName());
        verify(disbursementRuleOutputPort).search(disbursementRule);
    }

    @Test
    void searchWithNullDisbursementRule() throws MeedlException {
        assertThrows(MeedlException.class, () -> disbursementRuleService.search(null));
        verify(disbursementRuleOutputPort, never()).search(any());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\t" })
    void searchWithInvalidName(String invalidName) throws MeedlException {
        disbursementRule.setName(invalidName);

        assertThrows(MeedlException.class, () -> disbursementRuleService.search(disbursementRule));
        verify(disbursementRuleOutputPort, never()).search(any());
    }

    @Test
    void searchWithEmptyStatuses() throws MeedlException {
        // given
        disbursementRule.setActivationStatuses(Set.of()); // no statuses
        Page<DisbursementRule> expectedPage = new PageImpl<>(List.of(disbursementRule));

        when(disbursementRuleOutputPort.search(any(DisbursementRule.class)))
                .thenReturn(expectedPage);

        // when
        Page<DisbursementRule> result = disbursementRuleService.search(disbursementRule);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(disbursementRuleOutputPort).search(disbursementRule);
    }

}

