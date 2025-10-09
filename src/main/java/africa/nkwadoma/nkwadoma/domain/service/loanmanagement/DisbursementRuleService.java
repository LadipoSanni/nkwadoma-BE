package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.DisbursementRuleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.LoanDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Service
public class DisbursementRuleService  implements DisbursementRuleUseCase {
    private final DisbursementRuleOutputPort disbursementRuleOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final LoanOutputPort loanOutputPort;
    private final LoanDisbursementRuleOutputPort loanDisbursementRuleOutputPort;


    @Override
    public DisbursementRule createDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateObjectInstance(disbursementRule.getUserIdentity(), UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getUserIdentity().getId(), UserMessages.INVALID_USER_ID.getMessage());
        disbursementRule.validate();
        Boolean ruleExist = disbursementRuleOutputPort.existByNameIgnoreCase(disbursementRule.getName().trim());
        if (ruleExist){
            log.error("Disbursement rule already exist with this name{}", disbursementRule.getName());
            throw new MeedlException("Disbursement rule already exist with this name "+ disbursementRule.getName());
        }
        UserIdentity actor = userIdentityOutputPort.findById(disbursementRule.getUserIdentity().getId());
        DisbursementRule savedDisbursementRule = saveDisbursementRule(disbursementRule, actor);
        disbursementRule.setId(savedDisbursementRule.getId());
        if (ActivationStatus.PENDING_APPROVAL.equals(savedDisbursementRule.getActivationStatus())){
            asynchronousNotificationOutputPort.notifyAdminOfDisbursementRuleApproval(disbursementRule);
        }
        return savedDisbursementRule;
    }

    private DisbursementRule saveDisbursementRule(DisbursementRule disbursementRule, UserIdentity actor) throws MeedlException {
        log.info("The role of the user creating disbursement rule is {} email {} disbursement status {}", actor.getRole(), actor.getEmail(), disbursementRule.getActivationStatus());
        disbursementRule.setUserIdentity(actor);
        disbursementRule.setCreatedBy(actor.getId());
        disbursementRule.setName(disbursementRule.getName().trim());
        disbursementRule.setActivationStatus(
                disbursementRule.getUserIdentity().getRole().isMeedlSuperAdmin()
                        ? ActivationStatus.APPROVED
                        : ActivationStatus.PENDING_APPROVAL.equals(disbursementRule.getActivationStatus())
                        ? ActivationStatus.PENDING_APPROVAL
                        : ActivationStatus.INACTIVE);
        disbursementRule.setDateCreated(LocalDateTime.now());
        return disbursementRuleOutputPort.save(disbursementRule);
    }

    @Override
    public DisbursementRule updateDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        DisbursementRule foundDIsbursementRule = disbursementRuleOutputPort.findById(disbursementRule.getId());
        if (!ActivationStatus.APPROVED.equals(foundDIsbursementRule.getActivationStatus())) {
            log.info("Updating disbursement rule ");
            foundDIsbursementRule.setName(disbursementRule.getName());
            return disbursementRuleOutputPort.save(foundDIsbursementRule);
        }
        return foundDIsbursementRule;
    }
    @Override
    public DisbursementRule respondToDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        MeedlValidator.validateObjectInstance(disbursementRule.getUserIdentity(), UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getUserIdentity().getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateObjectInstance(disbursementRule.getActivationStatus(), "Disbursement rule decision is required");

        if (!ActivationStatus.APPROVED.equals(disbursementRule.getActivationStatus()) &&
                !ActivationStatus.DECLINED.equals(disbursementRule.getActivationStatus())){
            log.error("The response for disbursement rule with id {} was neither approved nor decline but was {}", disbursementRule.getId(), disbursementRule.getActivationStatuses());
            throw new MeedlException("Response can only be Approve or Decline");
        }
        DisbursementRule foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRule.getId());
        foundDisbursementRule.setActivationStatus(disbursementRule.getActivationStatus());
        log.info("Activation status on disbursement rule is {}", foundDisbursementRule.getActivationStatuses());
        return disbursementRuleOutputPort.save(foundDisbursementRule);
    }
    @Override
    public DisbursementRule viewDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        log.info("View disbursement rule with id {}", disbursementRule.getId());
        return disbursementRuleOutputPort.findById(disbursementRule.getId());
    }
    @Override
    public Page<DisbursementRule> viewAllDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());

        log.info("View all disbursement rules at service. With activation status {}", disbursementRule.getActivationStatuses());
        return disbursementRuleOutputPort.findAllDisbursementRule(disbursementRule);
    }

     @Override
    public void deleteDisbursementRuleById(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        log.info("Delete disbursement rule by id ");

        disbursementRuleOutputPort.deleteById(disbursementRule.getId());
    }

    @Override
    public Page<DisbursementRule> search(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateDataElement(disbursementRule.getName(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_NAME.getMessage());

        return disbursementRuleOutputPort.search(disbursementRule);
    }

    @Override
    public DisbursementRule applyDisbursementRuleToLoans(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        MeedlValidator.validateObjectInstance(disbursementRule.getUserIdentity(), UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getUserIdentity().getId(), UserMessages.INVALID_USER_ID.getMessage());
        disbursementRule.validateLoanIds();
        UserIdentity actor = userIdentityOutputPort.findById(disbursementRule.getUserIdentity().getId());
        disbursementRule.setUserIdentity(actor);
        DisbursementRule foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRule.getId());
        if (!ActivationStatus.APPROVED.equals(foundDisbursementRule.getActivationStatus())){
            log.error("Disbursement rule cannot be apply due to status {}", disbursementRule.getActivationStatus());
            throw new MeedlException("Disbursement rule must be approved to be applied");
        }
        for (String loanId : disbursementRule.getLoanIds()){
            Loan loan = loanOutputPort.findLoanById(loanId);
            List<LoanDisbursementRule> loanDisbursementRules =  loanDisbursementRuleOutputPort.findAllByLoanIdAndDisbursementRuleId(loanId, foundDisbursementRule.getId());
            if(ObjectUtils.isEmpty(loanDisbursementRules)) {
                LoanDisbursementRule loanDisbursementRule = createLoanDisbursementRule(loan, disbursementRule);
            }

        }
        return disbursementRule;
    }

    private LoanDisbursementRule createLoanDisbursementRule(Loan loan, DisbursementRule disbursementRule) throws MeedlException {
        LoanDisbursementRule loanDisbursementRule = LoanDisbursementRule.builder()
                .disbursementRule(disbursementRule)
                .loan(loan)
                .appliedBy(disbursementRule.getUserIdentity().getId())
                .activationStatus(ActivationStatus.ACTIVE)
                .dateApplied(LocalDateTime.now())
                .interval(disbursementRule.getInterval())
                .percentageDistribution(disbursementRule.getPercentageDistribution())
                .startDate(disbursementRule.getStartDate())
                .endDate(disbursementRule.getEndDate())
                .name(disbursementRule.getName())
                .build();

        return loanDisbursementRuleOutputPort.save(loanDisbursementRule);
    }
}
