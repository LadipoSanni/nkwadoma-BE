package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
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
        MeedlValidator.validateUUID(id, "Please provide a valid employee identification");
        OrganizationEmployeeEntity organizationEmployeeIdentity = employeeAdminEntityRepository.findById(id).orElseThrow(()->new IdentityException(USER_NOT_FOUND.getMessage()));
        return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(organizationEmployeeIdentity);
    }

    @Override
    public OrganizationEmployeeIdentity  findByEmployeeId(String employeeId) throws IdentityException {
      if(!StringUtils.isEmpty(employeeId)){
          OrganizationEmployeeEntity organizationEmployee = employeeAdminEntityRepository.findByMeedlUserId(employeeId);
          if (organizationEmployee == null){
              log.error("{} : ---- while search for employee by employee id : {}", ORGANIZATION_EMPLOYEE_NOT_FOUND.getMessage(), employeeId);
              throw new IdentityException(ORGANIZATION_EMPLOYEE_NOT_FOUND.getMessage());
          }
          OrganizationEmployeeIdentity organizationEmployeeIdentity =
                  organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(organizationEmployee);
          log.info("OrganizationEmployeeIdentity mapped from organization employee: {}", organizationEmployeeIdentity);
          return organizationEmployeeIdentity;
      }
      throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

    @Override
    public Page<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
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
        MeedlValidator.validateUUID(createdBy, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        OrganizationEmployeeEntity employeeEntity = employeeAdminEntityRepository.findByMeedlUserId(createdBy);
        if(ObjectUtils.isEmpty(employeeEntity)){
            log.error("creator not found : ---- while search for organization by createdBy : {}", createdBy);
            UserIdentity foundUser = identityManagerOutputPort.getUserById(createdBy);
            if(ObjectUtils.isEmpty(foundUser)) {
                log.error("User not found on keycloak either {} ",createdBy);
                throw new IdentityException("Please register on our platform or contact your admin.");
            }
            log.error("User found on keycloak. User id {} user email {}", foundUser.getId(), foundUser.getEmail());
            throw new IdentityException("User with email "+foundUser.getEmail()+" can not perform this action. Please contact your admin");
        }
        log.info("The employee found using the created by:  {}", employeeEntity.getId());
        return organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(employeeEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "Please provide a valid organization employee identification");
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
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        List<OrganizationEmployeeEntity> employeeEntities = employeeAdminEntityRepository.findAllByOrganization(organizationId);
        return employeeEntities.stream()
                .map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity)
                .toList();
    }

    @Override
    public Page<OrganizationEmployeeIdentity> findEmployeesByNameAndRole(OrganizationIdentity
            organizationIdentity, IdentityRole identityRole) throws MeedlException {
        MeedlValidator.validateUUID(organizationIdentity.getId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectInstance(identityRole, INVALID_VALID_ROLE.getMessage());
        MeedlValidator.validatePageNumber(organizationIdentity.getPageNumber());
        MeedlValidator.validatePageSize(organizationIdentity.getPageSize());

        Pageable pageRequest = PageRequest.of(organizationIdentity.getPageNumber(),organizationIdentity.getPageSize());
        Page<OrganizationEmployeeEntity> organizationEmployeeEntities =
                employeeAdminEntityRepository.findByOrganizationIdAndRoleAndNameFragment
                        (organizationIdentity.getId(),identityRole,organizationIdentity.getName(),pageRequest);
        return organizationEmployeeEntities.map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity);
    }

    @Override
    public List<OrganizationEmployeeIdentity> findAllOrganizationEmployees(String organizationId) {
        List<OrganizationEmployeeEntity> organizationEmployeeEntities = employeeAdminEntityRepository.findByOrganization(organizationId);
        return organizationEmployeeEntities.stream().map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity).toList();
    }

    @Override
    public Page<OrganizationEmployeeIdentity> findAllAdminInOrganization(String organizationId, OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
//        MeedlValidator.validateObjectInstance(identityRole, IdentityMessages.INVALID_VALID_ROLE.getMessage());
        MeedlValidator.validatePageNumber(organizationEmployeeIdentity.getPageNumber());
        MeedlValidator.validatePageSize(organizationEmployeeIdentity.getPageSize());

        Pageable pageRequest = PageRequest.of(organizationEmployeeIdentity.getPageNumber(),organizationEmployeeIdentity.getPageSize());
        Page<OrganizationEmployeeEntity> organizationEmployeeEntities =
                employeeAdminEntityRepository.findAllByOrganizationIdAndMeedlUserRoles(organizationId,organizationEmployeeIdentity.getIdentityRoles(),pageRequest);
        return organizationEmployeeEntities.map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity);
    }

    @Override
    public Page<OrganizationEmployeeIdentity> findAllEmployeesInOrganization(String organizationId,String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<OrganizationEmployeeEntity> organizationEmployeeEntities =
                employeeAdminEntityRepository.findEmployeeInOrganizationbByIdAndName(organizationId,name,pageRequest);
        return organizationEmployeeEntities.map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity);
    }

    @Override
    public Optional<OrganizationEmployeeIdentity> findByMeedlUserId(String meedlUserId) throws MeedlException {
        MeedlValidator.validateUUID(meedlUserId, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        OrganizationEmployeeEntity employeeEntity = employeeAdminEntityRepository.findByMeedlUserId(meedlUserId);
        if(!ObjectUtils.isEmpty(employeeEntity)){
            log.info("The employee found using the meedl user id:  {}", employeeEntity.getId());
            return Optional.of(organizationEmployeeIdentityMapper.toOrganizationEmployeeIdentity(employeeEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<OrganizationEmployeeIdentity> findAllEmployeesInOrganizationByOrganizationIdAndRole(String organizationId, IdentityRole identityRole) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectInstance(identityRole, IdentityMessages.INVALID_VALID_ROLE.getMessage());

        List<OrganizationEmployeeEntity> organizationEmployeeEntities =
                employeeAdminEntityRepository.findOrganizationEmployeeEntityByOrganizationAndMeedlUserRole(organizationId,identityRole);
        return organizationEmployeeEntities.stream().map(organizationEmployeeIdentityMapper::toOrganizationEmployeeIdentity).toList();
    }


}
