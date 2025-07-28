package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class OrganizationLoanDetail {



    private String id;
    private OrganizationIdentity organization;
    private BigDecimal amountRequested = BigDecimal.ZERO;
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    private BigDecimal amountReceived = BigDecimal.ZERO;
    private BigDecimal amountRepaid = BigDecimal.ZERO;
    private BigDecimal interestIncurred = BigDecimal.ZERO;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(organization, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
    }


}
