package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@ToString
public class CohortLoanDetail {
    private String id;
    private Cohort cohort;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private BigDecimal totalOutstandingAmount = BigDecimal.ZERO;
    private BigDecimal totalAmountReceived  = BigDecimal.ZERO;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;
    private BigDecimal totalInterestIncurred  = BigDecimal.ZERO;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.CREATED_BY_CANNOT_BE_EMPTY.getMessage());
    }

}
