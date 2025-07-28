package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
@Builder
public class ProgramLoanDetail {


    private String id;
    private BigDecimal amountRequested = BigDecimal.ZERO;
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    private BigDecimal amountReceived = BigDecimal.ZERO;
    private BigDecimal amountRepaid = BigDecimal.ZERO;
    private BigDecimal interestIncurred = BigDecimal.ZERO;
    private Program program;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
    }
}