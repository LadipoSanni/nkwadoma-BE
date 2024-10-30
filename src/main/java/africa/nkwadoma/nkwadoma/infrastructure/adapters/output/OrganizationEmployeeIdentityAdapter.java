package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;

@RequiredArgsConstructor
@Slf4j
public class OrganizationEmployeeIdentityAdapter implements OrganizationEmployeeIdentityOutputPort {
    private final EmployeeAdminEntityRepository employeeAdminEntityRepository;
    private final OrganizationEmployeeIdentityMapper organizationEmployeeIdentityMapper;

    @Override
    public OrganizationEmployeeIdentity save(OrganizationEmployeeIdentity organizationEmployeeIdentity) {
        OrganizationEmployeeEntity organizationEmployeeEntity = organizationEmployeeIdentityMapper.toOrganizationEmployeeEntity(organizationEmployeeIdentity);
        organizationEmployeeEntity = employeeAdminEntityRepository.save(organizationEmployeeEntity);
        return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(organizationEmployeeEntity);
    }

    @Override
    public OrganizationEmployeeIdentity findById(String id)throws MeedlException {
        MeedlValidator.validateUUID(id);
        OrganizationEmployeeEntity organizationEmployeeIdentity = employeeAdminEntityRepository.findById(id).orElseThrow(()->new IdentityException(USER_NOT_FOUND.getMessage()));
        return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(organizationEmployeeIdentity);
    }

    @Override
    public OrganizationEmployeeIdentity findByEmployeeId(String employeeId) throws MeedlException {
      if(!StringUtils.isEmpty(employeeId)){
          OrganizationEmployeeEntity organization = employeeAdminEntityRepository.findByMiddlUserId(employeeId);
          if (organization == null){
              log.error("{} : ---- while search for organization by employee id : {}",ORGANIZATION_NOT_FOUND.getMessage(), employeeId);
              throw new IdentityException(ORGANIZATION_NOT_FOUND.getMessage());
          }
          return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(organization);
      }
        throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

    @Override
    public OrganizationEmployeeIdentity findByCreatedBy(String createdBy) throws MeedlException {
        MeedlValidator.validateUUID(createdBy);
        OrganizationEmployeeEntity employeeEntity = employeeAdminEntityRepository.findByMiddlUserId(createdBy);
        if(ObjectUtils.isEmpty(employeeEntity)){
            log.error("creator not found : ---- while search for organization by createdBy : {}", createdBy);
            throw new IdentityException(MeedlMessages.NON_EXISTING_CREATED_BY.getMessage());
        }
        log.info("The employee found using the created by:  {}", employeeEntity.getId());
        return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(employeeEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateDataElement(id);
        OrganizationEmployeeEntity employeeEntity = employeeAdminEntityRepository.findById(id).
                orElseThrow(()-> new IdentityException(USER_NOT_FOUND.getMessage()));
        employeeAdminEntityRepository.delete(employeeEntity);
    }

    @Override
    public List<OrganizationEmployeeIdentity> findAllByOrganization(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        List<OrganizationEmployeeEntity> employeeEntities = employeeAdminEntityRepository.findAllByOrganization(organizationId);
        return employeeEntities.stream()
                .map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity)
                .toList();
    }
}
