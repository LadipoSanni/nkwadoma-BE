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
    private BigDecimal amountRequested = BigDecimal.ZERO;
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    private BigDecimal amountReceived  = BigDecimal.ZERO;
    private BigDecimal amountRepaid = BigDecimal.ZERO;
    private BigDecimal interestIncurred  = BigDecimal.ZERO;
    private Cohort cohort;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.CREATED_BY_CANNOT_BE_EMPTY.getMessage());
    }

}
