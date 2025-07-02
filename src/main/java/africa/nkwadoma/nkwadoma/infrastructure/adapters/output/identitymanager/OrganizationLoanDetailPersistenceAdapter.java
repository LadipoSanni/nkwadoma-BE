package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.OrganizationLoanDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationLoanDetailPersistenceAdapter  implements OrganizationLoanDetailOutputPort {

    private final OrganizationLoanDetailMapper organizationLoanDetailMapper;
    private final OrganizationLoanDetailRepository organizationLoanDetailRepository;

    @Override
    public OrganizationLoanDetail save(OrganizationLoanDetail organizationLoanDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationLoanDetail,"Program loan detail cannot be empty");
        organizationLoanDetail.validate();

        OrganizationLoanDetailEntity programLoanDetailEntity =
                organizationLoanDetailMapper.toOrganizationLoanEntity(organizationLoanDetail);

        programLoanDetailEntity = organizationLoanDetailRepository.save(programLoanDetailEntity);
        return organizationLoanDetailMapper.toOrganizationLoanDetail(programLoanDetailEntity);
    }

    @Override
    public OrganizationLoanDetail findByOrganizationId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());

        OrganizationLoanDetailEntity programLoanDetailEntity =
                organizationLoanDetailRepository.findByOrganizationId(id);
        return organizationLoanDetailMapper.toOrganizationLoanDetail(programLoanDetailEntity);
    }

    @Override
    public void delete(String loanDetailsId) throws MeedlException {
        MeedlValidator.validateUUID(loanDetailsId,"Program loan detail id cannot be empty");
        organizationLoanDetailRepository.deleteById(loanDetailsId);
    }
}
