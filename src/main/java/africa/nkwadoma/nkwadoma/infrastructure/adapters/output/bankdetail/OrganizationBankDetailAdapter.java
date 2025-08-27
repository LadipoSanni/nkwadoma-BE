package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.OrganizationBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@AllArgsConstructor
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
