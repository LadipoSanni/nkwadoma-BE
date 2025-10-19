package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.DisbursementRuleUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.LoanDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement.DisbursementRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bouncycastle.util.MemoableResetException;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final DisbursementRuleMapper disbursementRuleMapper;
    private final LoanOfferOutputPort loanOfferOutputPort;


    @Override
    public DisbursementRule setUpDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
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
    public DisbursementRule editDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        disbursementRule.validate();
        DisbursementRule foundDIsbursementRule = disbursementRuleOutputPort.findById(disbursementRule.getId());
        if (!ActivationStatus.APPROVED.equals(foundDIsbursementRule.getActivationStatus())) {
            log.info("Updating disbursement rule ");

            validateRuleNameToUpdate(disbursementRule, foundDIsbursementRule);
            ActivationStatus activationStatus = foundDIsbursementRule.getActivationStatus();
            disbursementRuleMapper.edit(foundDIsbursementRule, disbursementRule);
            foundDIsbursementRule.setActivationStatus(activationStatus);
            return disbursementRuleOutputPort.save(foundDIsbursementRule);
        }
        return foundDIsbursementRule;
    }

    private void validateRuleNameToUpdate(DisbursementRule disbursementRule, DisbursementRule foundDIsbursementRule) throws MeedlException {
        if (!disbursementRule.getName().equalsIgnoreCase(foundDIsbursementRule.getName())) {
            boolean ruleExist = disbursementRuleOutputPort.existByNameIgnoreCase(disbursementRule.getName());
            if (ruleExist) {
                log.error("Cannot update disbursement rule to {} from {} because rule with this name already exist", disbursementRule.getName(), foundDIsbursementRule.getName());
                throw new MeedlException("Disbursement rule already exist with this name " + foundDIsbursementRule.getName());
            }
        }
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
        DisbursementRule foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRule.getId());
        if (foundDisbursementRule.getNumberOfTimesApplied() > 0){
            log.error("Disbursement rule cannot be deleted, already applied. Number of times {}", foundDisbursementRule.getNumberOfTimesApplied());
            throw new MeedlException("Disbursement rule cannot be deleted, already applied.");
        }
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
        DisbursementRule foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRule.getId());
        foundDisbursementRule.setUserIdentity(actor);
        if (!ActivationStatus.APPROVED.equals(foundDisbursementRule.getActivationStatus())){
            log.error("Disbursement rule cannot be apply due to status {}", disbursementRule.getActivationStatus());
            throw new MeedlException("Disbursement rule must be approved to be applied");
        }

        List<Loan> loans = getAllLoansToApplyDisbursementTo(disbursementRule);
        checkIfDisbursementRuleCanBeAppliedToLoan(loans, foundDisbursementRule);
        applyDisbursementRuleToLoans(loans, foundDisbursementRule);
        return disbursementRule;
    }

    private void applyDisbursementRuleToLoans(List<Loan> loans, DisbursementRule foundDisbursementRule) throws MeedlException {
        int totalNumberApplied = 0;
        for (Loan loan : loans){
            List<LoanDisbursementRule> loanDisbursementRules =  loanDisbursementRuleOutputPort.findAllByLoanIdAndDisbursementRuleId(loan.getId(), foundDisbursementRule.getId());
            if(ObjectUtils.isEmpty(loanDisbursementRules)) {
                log.info("Applying disbursement rule to loan ...");
                LoanDisbursementRule loanDisbursementRule = createLoanDisbursementRule(loan, foundDisbursementRule);
                totalNumberApplied++;
            }
        }
        log.info("updating number of times disbursement rule hase been applied. Previous count is  {}", foundDisbursementRule.getNumberOfTimesApplied());
        foundDisbursementRule.setNumberOfTimesApplied(foundDisbursementRule.getNumberOfTimesApplied() + totalNumberApplied);
        disbursementRuleOutputPort.save(foundDisbursementRule);
        log.info("new number of times disbursement rule hase been applied is {}", foundDisbursementRule.getNumberOfTimesApplied());
    }
    public void removeDisbursementRule(DisbursementRule disbursementRule){

    }

    private List<Loan> getAllLoansToApplyDisbursementTo(DisbursementRule disbursementRule) {
        return disbursementRule.getLoanIds().stream()
                .map(loanId -> {
                    try {
                        return loanOutputPort.findLoanById(loanId);
                    } catch (MeedlException e) {
                        log.info("Unable to find loan with id {} while applying disbursement rule", loanId, e);
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private void checkIfDisbursementRuleCanBeAppliedToLoan(List<Loan> loans, DisbursementRule foundDisbursementRule) throws MeedlException {
        LocalDateTime latestDateInList = foundDisbursementRule.getDistributionDates().stream()
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new MeedlException("No valid dates found on disbursement distribution"));

        for (Loan loan : loans){
            LoanOffer loanOffer = loanOfferOutputPort.findLoanOfferById(loan.getLoanOfferId());

            if (loanOffer.getStartDate().isAfter(ChronoLocalDate.from(latestDateInList))) {
                throw new MeedlException(String.format(
                        "The cohort start date %s is beyond the latest allowed date %s.",
                        loanOffer.getStartDate(), latestDateInList
                ));
            }
        }
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
                .distributionDates(disbursementRule.getDistributionDates())
                .name(disbursementRule.getName())
                .build();

        return loanDisbursementRuleOutputPort.save(loanDisbursementRule);
    }
}
