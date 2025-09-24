package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.DisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DisbursementRuleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.DisbursementRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisbursementRuleRuleAdapter implements DisbursementRuleOutputPort {
    private final DisbursementRuleRepository disbursementRuleRepository;
    private final DisbursementRuleMapper disbursementRuleMapper;

    @Override
    public DisbursementRule save(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        disbursementRule.validate();
        DisbursementRuleEntity disbursementRuleEntity = disbursementRuleMapper.map(disbursementRule);
        disbursementRuleEntity = disbursementRuleRepository.save(disbursementRuleEntity);
        log.info("disbursement rule entity in adapter level. id: {}", disbursementRuleEntity.getId());
        return disbursementRuleMapper.map(disbursementRuleEntity);
    }

    @Override
    public DisbursementRule findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE.getMessage());
        DisbursementRuleEntity disbursementRuleEntity = disbursementRuleRepository.findById(id)
                .orElseThrow(()-> new MeedlException(DisbursementRuleMessages.DISBURSEMENT_RULE_NOT_FOUND.getMessage()));
        return disbursementRuleMapper.map(disbursementRuleEntity);
    }

    @Override
    public void deleteById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE.getMessage());
        disbursementRuleRepository.deleteById(id);
    }
}
