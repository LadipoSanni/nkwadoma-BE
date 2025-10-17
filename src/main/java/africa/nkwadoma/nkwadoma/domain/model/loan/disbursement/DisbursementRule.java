package africa.nkwadoma.nkwadoma.domain.model.loan.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.HUNDRED_PERCENT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.TWO_DECIMAL_PLACE;

@Slf4j
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
    private List<Double> percentageDistribution;
    private List<LocalDateTime> distributionDates;
    private String createdBy;
    private LocalDateTime dateUpdated;
    private LocalDateTime dateCreated;
    private int numberOfUsage;
    private int numberOfTimesApplied;
    private int numberOfTimesAdjusted;
    private ActivationStatus activationStatus;
    private Set<ActivationStatus> activationStatuses;
    private UserIdentity userIdentity;

    private List<String> loanIds;

    private int pageNumber;
    private int pageSize;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(name, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_NAME.getMessage());
        validatePercentageDistributionAndDate(this.percentageDistribution);
    }
    public void validateActivationStatus() throws MeedlException {
        MeedlValidator.validateObjectInstance(activationStatus, DisbursementRuleMessages.INVALID_DISBURSEMENT_RULE_ACTIVATION_STATUS.getMessage() );
    }

    public void validatePercentageDistributionAndDate(List<Double> distributions) throws MeedlException {
        if (distributions == null || distributions.isEmpty()) {
            log.info("Percentage distribution cannot be empty");
            throw new MeedlException("Percentage distribution cannot be empty");
        }
        if (this.distributionDates == null || this.distributionDates.isEmpty()){
            log.info("Distribution dates is required {}", this.distributionDates);
            throw new MeedlException("Distribution dates is required");
        }
        if (this.distributionDates.size() != this.percentageDistribution.size()){
            log.info("Ensure that each distribution date aligns with its corresponding percentage distribution\"");
            throw new MeedlException("Ensure that each distribution date aligns with its corresponding percentage distribution");
        }

        for (Double distribution: distributions){
            if (ObjectUtils.isEmpty(distribution)){
                log.info("Distribution percentage is required {}", distribution);
                throw new MeedlException("Distribution percentage is required");
            }
        }

        for (LocalDateTime distributionDate: this.distributionDates){
            if (ObjectUtils.isEmpty(distributionDate)){
                log.info("Distribution date is required {}", distributionDate);
                throw new MeedlException("Distribution date is required");
            }
        }

        BigDecimal sum = distributions.stream()
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
