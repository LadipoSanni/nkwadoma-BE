package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;

import java.util.List;

public interface LoanDisbursementRuleOutputPort {
    LoanDisbursementRule save(LoanDisbursementRule loanDisbursementRule) throws MeedlException;

    LoanDisbursementRule findById(LoanDisbursementRule loanDisbursementRule) throws MeedlException;

    List<LoanDisbursementRule> findAllByLoanIdAndDisbursementRuleId(String loanId, String disbursementRuleId) throws MeedlException;

    void deleteById(String loanDisbursementRuleId) throws MeedlException;

    List<LoanDisbursementRule> findLoanDisbursementRuleByStatus(String loanId, DisbursementRuleStatus disbursementRuleStatus) throws MeedlException;

    void deleteAllLoanDisbursementRule(String loanId) throws MeedlException;
}
