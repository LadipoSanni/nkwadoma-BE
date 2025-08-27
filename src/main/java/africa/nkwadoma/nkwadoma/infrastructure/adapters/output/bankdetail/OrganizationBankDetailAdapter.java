package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.OrganizationBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail.OrganizationBankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail.OrganizationBankDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@AllArgsConstructor
public class OrganizationBankDetailAdapter implements OrganizationBankDetailOutputPort {
    private final OrganizationBankDetailRepository organizationBankDetailRepository;
    private final OrganizationBankDetailMapper organizationBankDetailMapper;


    @Override
    public OrganizationBankDetail save(OrganizationBankDetail organizationBankDetail) {
        return null;
    }
    @Override
    public List<BankDetail> findAllBankDetailOfOrganization(OrganizationIdentity organizationIdentity) {
        MeedlValidator.validateObjectInstance(OrganizationIdentity);
        return List.of();
    }

}
