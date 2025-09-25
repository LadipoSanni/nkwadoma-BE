package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductDisbursementRule;

public interface LoanProductDisbursementRuleOutputPort {
    LoanProductDisbursementRule save(LoanProductDisbursementRule loanProductDisbursementRule) throws MeedlException;

    LoanProductDisbursementRule findById(String loanProductDisbursementRuleId) throws MeedlException;

    void deleteById(String loanProductDisbursementRuleId) throws MeedlException;
}
