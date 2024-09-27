package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationOrganizationEmployeeIdentityAdapter implements OrganizationEmployeeIdentityOutputPort {
    private final EmployeeAdminEntityRepository employeeAdminEntityRepository;
    private final OrganizationEmployeeIdentityMapper organizationEmployeeIdentityMapper;
    @Override
    public OrganizationEmployeeIdentity save(OrganizationEmployeeIdentity organizationEmployeeIdentity) {
        OrganizationEmployeeEntity organizationEmployeeEntity = organizationEmployeeIdentityMapper.toEmploymentEntity(organizationEmployeeIdentity);
        organizationEmployeeEntity = employeeAdminEntityRepository.save(organizationEmployeeEntity);
        return organizationEmployeeIdentityMapper.toEmploymentAdminEntity(organizationEmployeeEntity);
    }
}
