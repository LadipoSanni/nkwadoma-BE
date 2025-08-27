package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

import java.util.List;

public interface OrganizationBankDetailOutputPort {
    List<BankDetail> findAllBankDetailOfOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;

    OrganizationBankDetail save(OrganizationBankDetail organizationBankDetail) throws MeedlException;

    OrganizationBankDetail findApprovedBankDetailByOrganizationId(OrganizationIdentity organizationIdentity) throws MeedlException;

    void deleteById(String organizationBankDetailId) throws MeedlException;
}
