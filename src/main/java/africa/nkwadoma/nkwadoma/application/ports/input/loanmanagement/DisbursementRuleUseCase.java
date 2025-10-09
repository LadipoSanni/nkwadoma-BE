package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import org.springframework.data.domain.Page;

public interface DisbursementRuleUseCase {
    DisbursementRule createDisbursementRule(DisbursementRule disbursementRule) throws MeedlException;

    DisbursementRule respondToDisbursementRule(DisbursementRule disbursementRule) throws MeedlException;

    DisbursementRule updateDisbursementRule(DisbursementRule disbursementRule) throws MeedlException;

    DisbursementRule viewDisbursementRule(DisbursementRule disbursementRule) throws MeedlException;

    Page<DisbursementRule> viewAllDisbursementRule(DisbursementRule disbursementRule) throws MeedlException;

    void deleteDisbursementRuleById(DisbursementRule disbursementRule) throws MeedlException;

    Page<DisbursementRule> search(DisbursementRule disbursementRule) throws MeedlException;

    DisbursementRule applyDisbursementRuleToLoans(DisbursementRule disbursementRule) throws MeedlException;
}
