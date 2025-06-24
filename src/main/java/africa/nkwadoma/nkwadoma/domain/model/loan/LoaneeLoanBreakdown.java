package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoaneeLoanBreakdown {

    private String loaneeLoanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount ;
    private String currency;
    private CohortLoanee cohortLoanee;

    public void validate() throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanBreakdownId, "Please provide a valid loanee's loan break down identification.");
        MeedlValidator.validateNegativeAmount(itemAmount,"Item");
        MeedlValidator.validateDataElement(itemName, "Item name is required");
    }
}
