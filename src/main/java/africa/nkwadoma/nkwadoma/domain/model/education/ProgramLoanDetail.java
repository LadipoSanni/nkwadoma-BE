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
    private Program program;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private BigDecimal totalOutstandingAmount = BigDecimal.ZERO;
    private BigDecimal totalAmountReceived = BigDecimal.ZERO;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;
    private BigDecimal totalInterestIncurred = BigDecimal.ZERO;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
    }
}