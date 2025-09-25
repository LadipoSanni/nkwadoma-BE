package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import org.springframework.data.domain.Page;

public interface DisbursementRuleOutputPort {
    DisbursementRule save(DisbursementRule disbursementRule) throws MeedlException;

    DisbursementRule findById(String id) throws MeedlException;

    void deleteById(String id) throws MeedlException;

    Page<DisbursementRule> findAllDisbursementRule(DisbursementRule disbursementRule) throws MeedlException;
}
