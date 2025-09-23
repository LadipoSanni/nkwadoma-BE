package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.DisbursementRuleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.DisbursementRuleRepository;

public class DisbursementRuleRuleAdapter implements DisbursementRuleOutputPort {
    private DisbursementRuleRepository disbursementRuleRepository;
    private DisbursementRuleMapper disbursementRuleMapper;

    public DisbursementRule save (DisbursementRule disbursementRule) throws MeedlException {
        MeedlValidator.validateObjectInstance(disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        disbursementRule.validate();

    }
}
