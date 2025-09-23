package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;

public interface DisbursementRuleOutputPort {
    DisbursementRule save(DisbursementRule disbursementRule) throws MeedlException;

    DisbursementRule findById(String id);
}
