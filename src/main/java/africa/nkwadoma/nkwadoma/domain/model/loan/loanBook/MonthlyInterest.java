package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class MonthlyInterest {

    private String id;
    private LocalDateTime createdAt;
    private BigDecimal interest;
    private LoaneeLoanDetail loaneeLoanDetail;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(createdAt,"Created at cannot be empty");
        MeedlValidator.validateBigDecimalDataElement(interest, "Interest cannot be empty");
        MeedlValidator.validateObjectInstance(loaneeLoanDetail, "Loanee loan detail cannot be empty");
    }
}
