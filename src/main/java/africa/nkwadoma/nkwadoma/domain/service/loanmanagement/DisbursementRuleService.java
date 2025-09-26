package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.DisbursementRuleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Service
public class DisbursementRuleService  implements DisbursementRuleUseCase {
    private final DisbursementRuleOutputPort disbursementRuleOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;


    @Override
    public DisbursementRule createDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateObjectInstance(disbursementRule.getUserIdentity(), UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getUserIdentity().getId(), UserMessages.INVALID_USER_ID.getMessage());
        disbursementRule.validate();
        Boolean ruleExist = disbursementRuleOutputPort.existByName(disbursementRule.getName());
        if (ruleExist){
            log.error("Disbursement rule already exist with this name"+ disbursementRule.getName());
            throw new MeedlException("Disbursement rule already exist with this name "+ disbursementRule.getName());
        }
        UserIdentity actor = userIdentityOutputPort.findById(disbursementRule.getUserIdentity().getId());
        disbursementRule.setUserIdentity(actor);
        DisbursementRule savedDisbursementRule;
            disbursementRule.setActivationStatus(
                    disbursementRule.getUserIdentity().getRole().isMeedlSuperAdmin()
                            ? ActivationStatus.APPROVED
                            : ActivationStatus.PENDING_APPROVAL.equals(disbursementRule.getActivationStatus())
                            ? ActivationStatus.PENDING_APPROVAL
                            : ActivationStatus.INACTIVE);
            savedDisbursementRule = disbursementRuleOutputPort.save(disbursementRule);
        if (ActivationStatus.PENDING_APPROVAL.equals(savedDisbursementRule.getActivationStatus())){
            asynchronousNotificationOutputPort.notifyAdminOfDisbursementRuleApproval(disbursementRule);
        }

        return savedDisbursementRule;
    }

//    @Override
//    public DisbursementRule setUpLoanProductDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
//        log.info("Saving loan product disbursement rules");
//        disbursementRule.setActivationStatus(disbursementRule.getUserIdentity().getRole().isMeedlSuperAdmin()
//                ? ActivationStatus.APPROVED
//                : ActivationStatus.PENDING_APPROVAL);
//        DisbursementRule savedDisbursementRule = disbursementRuleOutputPort.save(disbursementRule);
//        disbursementRule.setId(savedDisbursementRule.getId());
//        log.info("Saving loan product disbursement rules from loan product");
//        LoanProductDisbursementRule loanProductDisbursementRule = LoanProductDisbursementRule.builder()
//                .disbursementRule(savedDisbursementRule)
//                .loanProduct(disbursementRule.getLoanProduct())
//                .build();
//        loanProductDisbursementRuleOutputPort.save(loanProductDisbursementRule);
//       return disbursementRule;
//    }

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
}
