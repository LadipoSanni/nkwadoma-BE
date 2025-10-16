package africa.nkwadoma.nkwadoma.domain.model.loan.disbursement;


import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.DisbursementRuleEntity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class LoanDisbursementRule {
    private String id;
    private Loan loan;
    private DisbursementRule disbursementRule;

    private String name;
    private String appliedBy;
    private DisbursementInterval interval;
    private ActivationStatus activationStatus;
    private List<Double> percentageDistribution;
    private List<LocalDateTime> distributionDates;
    private int numberOfTimesAdjusted;
    private LocalDateTime dateLastAdjusted;
    private LocalDateTime dateApplied;
    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.loan, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(this.disbursementRule, DisbursementRuleMessages.EMPTY_DISBURSEMENT_RULE.getMessage());
        MeedlValidator.validateUUID(this.loan.getId(), LoanMessages.INVALID_LOAN_ID.getMessage());
        MeedlValidator.validateUUID(this.disbursementRule.getId(), DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ID.getMessage());
    }
}
