package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.OrganizationBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;

import java.util.List;

public class OrganizationBankDetailAdapter implements OrganizationBankDetailOutputPort {
    @Override
    public List<BankDetail> findAllBankDetailOfOrganization(OrganizationIdentity organizationIdentity) {
        return List.of();
    }

    @Override
    public OrganizationBankDetail save(OrganizationBankDetail organizationBankDetail) {
        return null;
    }
}
