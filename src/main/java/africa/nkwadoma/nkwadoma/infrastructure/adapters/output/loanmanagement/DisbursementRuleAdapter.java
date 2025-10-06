package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.DisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DisbursementRuleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.DisbursementRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DisbursementRuleAdapter implements DisbursementRuleOutputPort {
    private final DisbursementRuleRepository disbursementRuleRepository;
    private final DisbursementRuleMapper disbursementRuleMapper;

    @Override
    public DisbursementRule save(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        disbursementRule.validate();
        disbursementRule.validateActivationStatus();
        DisbursementRuleEntity disbursementRuleEntity = disbursementRuleMapper.map(disbursementRule);
        disbursementRuleEntity = disbursementRuleRepository.save(disbursementRuleEntity);
        log.info("disbursement rule entity in adapter level. id: {}", disbursementRuleEntity.getId());
        return disbursementRuleMapper.map(disbursementRuleEntity);
    }

    @Override
    public DisbursementRule findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        DisbursementRuleEntity disbursementRuleEntity = disbursementRuleRepository.findById(id)
                .orElseThrow(()-> new MeedlException(DisbursementRuleMessages.DISBURSEMENT_RULE_NOT_FOUND.getMessage()));
        return disbursementRuleMapper.map(disbursementRuleEntity);
    }

    @Override
    public void deleteById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
        disbursementRuleRepository.deleteById(id);
    }

    @Override
    public Page<DisbursementRule> findAllDisbursementRule(DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());

        log.info("View all disbursement rules at service. With activation status {}", disbursementRule.getActivationStatuses());
        Pageable pageRequest = PageRequest.of(disbursementRule.getPageNumber(), disbursementRule.getPageSize());

        Page<DisbursementRuleEntity> disbursementRuleEntities =
                disbursementRuleRepository.findAllDisbursementRuleByActivationStatuses(disbursementRule.getActivationStatuses(), pageRequest);

        return disbursementRuleEntities.map(disbursementRuleMapper::map);
    }

    @Override
    public Boolean existByNameIgnoreCase(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_NAME.getMessage());
        return disbursementRuleRepository.existsByNameIgnoreCase(name);
    }
}
