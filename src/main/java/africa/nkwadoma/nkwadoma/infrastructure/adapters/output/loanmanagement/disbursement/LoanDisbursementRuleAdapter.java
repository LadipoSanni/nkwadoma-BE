package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.disbursement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.LoanDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.LoanDisbursementRuleMessage;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement.LoanDisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.LoanDisbursementRuleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement.LoanDisbursementRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class LoanDisbursementRuleAdapter implements LoanDisbursementRuleOutputPort {

    private final LoanDisbursementRuleRepository loanDisbursementRuleRepository;
    private final LoanDisbursementRuleMapper loanDisbursementRuleMapper;

    @Override
    public LoanDisbursementRule save(LoanDisbursementRule loanDisbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanDisbursementRule, LoanDisbursementRuleMessage.EMPTY_LOAN_DISBURSEMENT_RULE.getMessage());
        loanDisbursementRule.validate();
        LoanDisbursementRuleEntity loanDisbursementRuleEntity = loanDisbursementRuleMapper.map(loanDisbursementRule);
        loanDisbursementRuleEntity = loanDisbursementRuleRepository.save(loanDisbursementRuleEntity);
        return loanDisbursementRuleMapper.map(loanDisbursementRuleEntity);
    }
    @Override
    public LoanDisbursementRule findById(LoanDisbursementRule loanDisbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanDisbursementRule, LoanDisbursementRuleMessage.EMPTY_LOAN_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(loanDisbursementRule.getId(), LoanDisbursementRuleMessage.INVALID_LOAN_DISBURSEMENT_RULE_ID.getMessage());
        LoanDisbursementRuleEntity loanDisbursementRuleEntity =
                loanDisbursementRuleRepository.findById(loanDisbursementRule.getId())
                        .orElseThrow(()-> new MeedlException(LoanDisbursementRuleMessage.LOAN_DISBURSEMENT_RULE_NOT_FOUND.getMessage()));
        return loanDisbursementRuleMapper.map(loanDisbursementRuleEntity);
    }

    @Override
    public List<LoanDisbursementRule> findAllByLoanIdAndDisbursementRuleId(String loanId, String disbursementRuleId) throws MeedlException {
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());
        MeedlValidator.validateUUID(disbursementRuleId, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        log.info("Loan id == {} disbursement rule id === {}", loanId, disbursementRuleId);
        List<LoanDisbursementRuleEntity> loanDisbursementRuleEntities =
                loanDisbursementRuleRepository.findAllByLoanEntity_IdAndDisbursementRuleEntity_Id(loanId, disbursementRuleId);

        return loanDisbursementRuleEntities.stream()
                .map(loanDisbursementRuleMapper::map)
                .toList();
    }
    @Override
    public void deleteById(String loanDisbursementRuleId) throws MeedlException {
        MeedlValidator.validateUUID(loanDisbursementRuleId, LoanDisbursementRuleMessage.INVALID_LOAN_DISBURSEMENT_RULE_ID.getMessage());
        loanDisbursementRuleRepository.deleteById(loanDisbursementRuleId);
    }


}
