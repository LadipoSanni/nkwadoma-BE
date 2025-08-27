package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

import java.util.List;

public interface OrganizationBankDetailOutputPort {
    List<BankDetail> findAllBankDetailOfOrganization(OrganizationIdentity organizationIdentity);

    OrganizationBankDetail save(OrganizationBankDetail organizationBankDetail);
}
