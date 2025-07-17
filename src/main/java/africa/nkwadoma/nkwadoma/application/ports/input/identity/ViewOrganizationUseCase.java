package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.springframework.data.domain.*;

public interface ViewOrganizationUseCase {
    Page<OrganizationIdentity> search(OrganizationIdentity organizationIdentity) throws MeedlException;

    OrganizationIdentity viewOrganizationDetails(String organizationId, String userId) throws MeedlException;
    OrganizationIdentity viewTopOrganizationByLoanRequestCount() throws MeedlException;
    Page<OrganizationIdentity> viewAllOrganizationsLoanMetrics(LoanType loanType,int pageSize , int pageNumber) throws MeedlException;
    Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;
    Page<OrganizationIdentity> viewAllOrganizationByStatus(OrganizationIdentity organizationIdentity, ActivationStatus activationStatus) throws MeedlException;
}
