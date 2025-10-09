package africa.nkwadoma.nkwadoma.domain.model.loan.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.HUNDRED_PERCENT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.TWO_DECIMAL_PLACE;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class DisbursementRule {
    private String id;
    private String name;
    private DisbursementInterval interval;
    private int numberOfUsage;
    private List<Double> percentageDistribution;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime dateCreated;
    private String createdBy;
    private ActivationStatus activationStatus;
    private Set<ActivationStatus> activationStatuses;
    private UserIdentity userIdentity;

    private List<String> loanIds;

    private int pageNumber;
    private int pageSize;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(name, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_NAME.getMessage());
        validatePercentageDistribution(this.percentageDistribution);

//        MeedlValidator.validateDataElement(query, DisbursementRuleMessages.DISBURSEMENT_RULE_QUERY_CANNOT_BE_EMPTY.getMessage());
    }
    public void validateActivationStatus() throws MeedlException {
        MeedlValidator.validateObjectInstance(activationStatus, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ACTIVATION_STATUS.getMessage() );
    }

    public static void validatePercentageDistribution(List<Double> distribution) throws MeedlException {
        if (distribution == null || distribution.isEmpty()) {
            throw new MeedlException("Percentage distribution cannot be empty");
        }

        BigDecimal sum = distribution.stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Round to 2 decimal places to avoid floating-point noise
        sum = sum.setScale(TWO_DECIMAL_PLACE, RoundingMode.HALF_UP);

        if (sum.compareTo(HUNDRED_PERCENT) != BigDecimal.ZERO.intValue()) {
            throw new MeedlException(
                    "Invalid distribution: percentages must sum up to 100%. Current total = " + sum);
        }
    }

    public void validateLoanIds() throws MeedlException {
        for (String id : loanIds){
            MeedlValidator.validateUUID(id, "Loan to apply disbursement rule to is invalid");
        }
    }
}
