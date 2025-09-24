package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanProductDisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductDisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductDisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DisbursementRuleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductDisbursementRuleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductDisbursementRuleRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanProductDisbursementRuleAdapter implements LoanProductDisbursementRuleOutputPort {

    private final LoanProductDisbursementRuleRuleRepository loanProductDisbursementRuleRuleRepository;
    private final LoanProductDisbursementRuleMapper loanProductDisbursementRuleMapper;
    @Override
    public LoanProductDisbursementRule save(LoanProductDisbursementRule loanProductDisbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProductDisbursementRule, LoanProductDisbursementRuleMessages.EMPTY_LOAN_PRODUCT_DISBURSEMENT_RULE.getMessage());
        loanProductDisbursementRule.validate();
        LoanProductDisbursementRuleEntity loanProductDisbursementRuleEntity = loanProductDisbursementRuleMapper.map(loanProductDisbursementRule);
        loanProductDisbursementRuleEntity = loanProductDisbursementRuleRuleRepository.save(loanProductDisbursementRuleEntity);
        log.info("disbursement rule entity in adapter level. id: {}", loanProductDisbursementRuleEntity.getId());
        return loanProductDisbursementRuleMapper.map(loanProductDisbursementRuleEntity);
    }

    @Override
    public LoanProductDisbursementRule findById(String loanProductDisbursementRuleId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductDisbursementRuleId, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE.getMessage());
        LoanProductDisbursementRuleEntity loanProductDisbursementRuleEntity = loanProductDisbursementRuleRuleRepository.findById(loanProductDisbursementRuleId)
                .orElseThrow(()-> new MeedlException(DisbursementRuleMessages.DISBURSEMENT_RULE_NOT_FOUND.getMessage()));
        return loanProductDisbursementRuleMapper.map(loanProductDisbursementRuleEntity);
    }

    @Override
    public void deleteById(String loanProductDisbursementRuleId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductDisbursementRuleId, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE.getMessage());
        loanProductDisbursementRuleRuleRepository.deleteById(loanProductDisbursementRuleId);
    }
}
