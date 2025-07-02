package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;

public interface OrganizationLoanDetailOutputPort {


    OrganizationLoanDetail save(OrganizationLoanDetail organizationLoanDetail) throws MeedlException;

    OrganizationLoanDetail findByOrganizationId(String id) throws MeedlException;

    void delete(String loanDetailsId) throws MeedlException;
}
