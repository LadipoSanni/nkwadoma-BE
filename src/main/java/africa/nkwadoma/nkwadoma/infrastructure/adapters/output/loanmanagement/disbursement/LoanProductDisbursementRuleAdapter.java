package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.disbursement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.LoanProductDisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanProductDisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement.LoanProductDisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductDisbursementRuleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement.LoanProductDisbursementRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanProductDisbursementRuleAdapter implements LoanProductDisbursementRuleOutputPort {

    private final LoanProductDisbursementRuleRepository loanProductDisbursementRuleRepository;
    private final LoanProductDisbursementRuleMapper loanProductDisbursementRuleMapper;
    @Override
    public LoanProductDisbursementRule save(LoanProductDisbursementRule loanProductDisbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProductDisbursementRule, LoanProductDisbursementRuleMessages.EMPTY_LOAN_PRODUCT_DISBURSEMENT_RULE.getMessage());
        loanProductDisbursementRule.validate();
        LoanProductDisbursementRuleEntity loanProductDisbursementRuleEntity = loanProductDisbursementRuleMapper.map(loanProductDisbursementRule);
        loanProductDisbursementRuleEntity = loanProductDisbursementRuleRepository.save(loanProductDisbursementRuleEntity);
        log.info("disbursement rule entity in adapter level. id: {}", loanProductDisbursementRuleEntity.getId());
        return loanProductDisbursementRuleMapper.map(loanProductDisbursementRuleEntity);
    }

    @Override
    public LoanProductDisbursementRule findById(String loanProductDisbursementRuleId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductDisbursementRuleId, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        LoanProductDisbursementRuleEntity loanProductDisbursementRuleEntity = loanProductDisbursementRuleRepository.findById(loanProductDisbursementRuleId)
                .orElseThrow(()-> new MeedlException(LoanProductDisbursementRuleMessages.LOAN_PRODUCT_DISBURSEMENT_RULE_NOT_FOUND.getMessage()));
        return loanProductDisbursementRuleMapper.map(loanProductDisbursementRuleEntity);
    }

    @Override
    public void deleteById(String loanProductDisbursementRuleId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductDisbursementRuleId, LoanProductDisbursementRuleMessages.INVALID_LOAN_PRODUCT_DISBURSEMENT_RULE_ID.getMessage());
        loanProductDisbursementRuleRepository.deleteById(loanProductDisbursementRuleId);
    }
}
