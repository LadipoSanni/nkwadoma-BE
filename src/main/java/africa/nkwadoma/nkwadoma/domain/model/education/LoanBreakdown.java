package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoanBreakdown {
    private String loanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount;
    private String currency;
    private Cohort cohort;

    public void validate() throws MeedlException {
        MeedlValidator.validateNegativeAmount(itemAmount,"Item");
        MeedlValidator.validateObjectName(itemName,"Item name cannot be empty","Item");
    }
}
