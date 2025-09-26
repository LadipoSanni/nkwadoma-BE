package africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PlatformRequest {
    private String id;
    private BigDecimal obligorLoanLimit;
    private String createdBy;
    private LocalDateTime requestTime;
    private int pageNumber;
    private int PageSize;

    public void validateObligorLoanLimitData() throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(obligorLoanLimit, "Obligor loan limit cannot be empty");
        MeedlValidator.validateUUID(createdBy, "Actor creating this request needs to be stated");
        MeedlValidator.validateObjectInstance(requestTime, "Time of request needs to be stated");
    }
}
