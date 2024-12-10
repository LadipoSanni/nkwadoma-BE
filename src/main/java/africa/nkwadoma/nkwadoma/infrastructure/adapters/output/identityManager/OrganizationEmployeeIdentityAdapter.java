package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;

import java.util.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMPTY_INPUT_FIELD_ERROR;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrganizationEmployeeIdentityAdapter implements OrganizationEmployeeIdentityOutputPort {
    private final EmployeeAdminEntityRepository employeeAdminEntityRepository;
    private final IdentityManagerOutputPort identityManagerOutputPort;
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
          OrganizationEmployeeEntity organization = employeeAdminEntityRepository.findByMeedlUserId(employeeId);
          if (organization == null){
              log.error("{} : ---- while search for organization by employee id : {}",ORGANIZATION_NOT_FOUND.getMessage(), employeeId);
              throw new IdentityException(ORGANIZATION_NOT_FOUND.getMessage());
          }
          return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(organization);
      }
        throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

    @Override
    public Page<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Page<OrganizationEmployeeProjection> organizationEmployees =
                employeeAdminEntityRepository.findAllByOrganization(
                        organizationId, PageRequest.of(pageNumber, pageSize));
        if (organizationEmployees.isEmpty()) {
            return Page.empty();
        }
        Page<OrganizationEmployeeIdentity> employeeIdentities = organizationEmployees
                .map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity);
        log.info("Mapped Organization employees: {}", employeeIdentities.getContent());
        return employeeIdentities;
    }

    @Override
    public OrganizationEmployeeIdentity findByCreatedBy(String createdBy) throws MeedlException {
        MeedlValidator.validateUUID(createdBy);
        OrganizationEmployeeEntity employeeEntity = employeeAdminEntityRepository.findByMeedlUserId(createdBy);
        if(ObjectUtils.isEmpty(employeeEntity)){
            log.error("creator not found : ---- while search for organization by createdBy : {}", createdBy);
            UserIdentity foundUser = identityManagerOutputPort.getUserById(createdBy);
            if(ObjectUtils.isEmpty(foundUser)) {
                log.error("User not found on keycloak either {} ",createdBy);
                throw new IdentityException("Please register on our platform or contact your admin.");
            }
            log.error("User found on keycloak. User id {} user email {}", foundUser.getId(), foundUser.getEmail());
            throw new IdentityException("User with email "+foundUser.getEmail()+" can no longer perform this action. Please contact your admin");
        }
        log.info("The employee found using the created by:  {}", employeeEntity.getId());
        return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(employeeEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        OrganizationEmployeeEntity employeeEntity = employeeAdminEntityRepository.findById(id).
                orElseThrow(()-> new IdentityException(USER_NOT_FOUND.getMessage()));
        employeeAdminEntityRepository.delete(employeeEntity);
    }

    @Transactional
    @Override
    public void deleteEmployee(String id) throws IdentityException {
        if (StringUtils.isEmpty(id)){
            throw new IdentityException(EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
        employeeAdminEntityRepository.deleteByMeedlUserId(id);
    }



    @Override
    public List<OrganizationEmployeeIdentity> findAllByOrganization(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        List<OrganizationEmployeeEntity> employeeEntities = employeeAdminEntityRepository.findAllByOrganization(organizationId);
        return employeeEntities.stream()
                .map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity)
                .toList();
    }

    @Override
    public List<OrganizationEmployeeIdentity> findEmployeesByNameAndRole(String organizationId, String name, IdentityRole identityRole) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        MeedlValidator.validateDataElement(name, "Admin name to search for is required.");
        MeedlValidator.validateObjectInstance(identityRole);
        List<OrganizationEmployeeEntity> organizationEmployeeEntities =
                employeeAdminEntityRepository.findByOrganizationIdAndRoleAndNameFragment
                        (organizationId,identityRole,name);
        return organizationEmployeeEntities.stream().map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity).toList();
    }

    @Override
    public List<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId) {
        List<OrganizationEmployeeEntity> organizationEmployeeEntities = employeeAdminEntityRepository.findByOrganization(organizationId);
        return organizationEmployeeEntities.stream().map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity).toList();
    }

    @Override
    public Page<OrganizationEmployeeIdentity> findAllAdminInOrganization(String organizationId, IdentityRole identityRole, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId);
        MeedlValidator.validateObjectInstance(identityRole);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);

        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<OrganizationEmployeeEntity> organizationEmployeeEntities =
                employeeAdminEntityRepository.findAllByOrganizationAndMeedlUserRole(organizationId,identityRole,pageRequest);
        return organizationEmployeeEntities.map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity);
    }


}
