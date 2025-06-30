package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;

import java.math.BigDecimal;

public class OrganizationLoanDetail {



    private String id;
    private OrganizationIdentity organization;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private BigDecimal totalOutstandingAmount = BigDecimal.ZERO;
    private BigDecimal totalAmountReceived = BigDecimal.ZERO;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(organization, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
    }
}
