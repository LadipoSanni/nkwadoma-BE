package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class DisbursementRule {
    private String id;
    private String name;
    private String query;
    private ActivationStatus activationStatus;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(name, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_NAME.getMessage());
        MeedlValidator.validateDataElement(query, DisbursementRuleMessages.DISBURSEMENT_RULE_QUERY_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(activationStatus, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ACTIVATION_STATUS.getMessage() );
    }
}
