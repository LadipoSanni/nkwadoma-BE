package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.ORGANIZATION_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages.INVALID_ORGANIZATION_ID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrganizationIdentityAdapter implements OrganizationIdentityOutputPort {
    private final OrganizationEntityRepository organizationEntityRepository;
    private final ServiceOfferEntityRepository serviceOfferEntityRepository;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationServiceOfferingRepository organizationServiceOfferingRepository;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final ProgramOutputPort programOutputPort;

    @Override
    public OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("Organization identity before saving {}", organizationIdentity);
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_MUST_NOT_BE_EMPTY.getMessage());
        organizationIdentity.validate();
        MeedlValidator.validateOrganizationUserIdentities(organizationIdentity.getOrganizationEmployees());

        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity.setInvitedDate(LocalDateTime.now());
        organizationEntity = organizationEntityRepository.save(organizationEntity);

        List<ServiceOfferingEntity> serviceOfferingEntities = saveServiceOfferingEntities(organizationIdentity);
        saveOrganizationServiceOfferings(serviceOfferingEntities, organizationEntity);
        log.info("Organization entity saved successfully {}", organizationEntity);
        List<ServiceOffering> savedServiceOfferings = organizationIdentityMapper.
                toServiceOfferingEntitiesServiceOfferings(serviceOfferingEntities);
        log.info("Organization entity saved successfully");

        organizationIdentity = organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
        organizationIdentity.setServiceOfferings(savedServiceOfferings);
        return organizationIdentity;
    }

    @Override
    public Optional<OrganizationEntity> findByRcNumber(String rcNumber) throws MeedlException {
        log.info("Find organization with rcNumber {}", rcNumber);
        MeedlValidator.validateDataElement(rcNumber, OrganizationMessages.RC_NUMBER_IS_REQUIRED.getMessage());
        return organizationEntityRepository.findByRcNumber(rcNumber);
    }

    private List<ServiceOfferingEntity> saveServiceOfferingEntities(OrganizationIdentity organizationIdentity) {
        List<ServiceOffering> serviceOfferings = organizationIdentity.getServiceOfferings();
        List<ServiceOfferingEntity> serviceOfferingEntity = organizationIdentityMapper.toServiceOfferingEntity(serviceOfferings);
        log.info("Saving all service offerings");
        return serviceOfferEntityRepository.saveAll(serviceOfferingEntity);
    }

    private void saveOrganizationServiceOfferings(List<ServiceOfferingEntity> serviceOfferingEntities, OrganizationEntity organizationEntity) {
        for (ServiceOfferingEntity foundServiceOffering : serviceOfferingEntities) {
            OrganizationServiceOfferingEntity organizationServiceOfferingEntity =
                    OrganizationServiceOfferingEntity.builder().organizationId(organizationEntity.getId()).
                            serviceOfferingEntity(foundServiceOffering).build();
            organizationServiceOfferingRepository.save(organizationServiceOfferingEntity);
        }
    }

    @Override
    public OrganizationIdentity findByEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        OrganizationEntity organizationEntity = organizationEntityRepository.findByEmail(email).
                orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND.getMessage()));
        return updateFoundOrganizationIdentityDetails(organizationEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, INVALID_ORGANIZATION_ID.getMessage());
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(id)
                .orElseThrow(()-> new IdentityException(ORGANIZATION_NOT_FOUND.getMessage()));
        List<Program> programs = programOutputPort.findAllProgramsByOrganizationId(organizationEntity.getId());
        programs.forEach(program -> {
            try {
                programOutputPort.deleteProgram(program.getId());
                log.info("Deleted program with id {} successfully while deleting organization with id {}", program.getId(), organizationEntity.getId());
            } catch (MeedlException e) {
                log.error("Error deleting program with id {}, while attempting to delete organization with id {}. Error message : {}", program, organizationEntity.getId(), e.getMessage());
            }
        });
        organizationEntityRepository.delete(organizationEntity);
    }

    @Override
    public OrganizationIdentity findById(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, INVALID_ORGANIZATION_ID.getMessage());
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(organizationId)
                .orElseThrow(()-> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        return updateFoundOrganizationIdentityDetails(organizationEntity);
    }
    @Override
    public OrganizationIdentity findOrganizationByCohortId(String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        OrganizationEntity organizationEntity = organizationEntityRepository.findByCohortId(cohortId)
                .orElseThrow(()-> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        return updateFoundOrganizationIdentityDetails(organizationEntity);
    }
    private OrganizationIdentity updateFoundOrganizationIdentityDetails(OrganizationEntity organizationEntity) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
        organizationIdentity.setServiceOfferings(getServiceOfferings(organizationIdentity.getId()));
        organizationIdentity.setOrganizationEmployees(organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationEntity.getId()));
        return organizationIdentity;
    }
    @Override
    public Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("Searching for all organizations at adapter level.");
        MeedlValidator.validateObjectInstance(organizationIdentity, "View all organization request can not be empty");
        MeedlValidator.validatePageSize(organizationIdentity.getPageSize());
        MeedlValidator.validatePageNumber(organizationIdentity.getPageNumber());
        Pageable pageRequest = PageRequest.of(organizationIdentity.getPageNumber(), organizationIdentity.getPageSize(), Sort.by(Sort.Direction.DESC, "invitedDate"));
        log.info("Page number: {}, page size: {}", organizationIdentity.getPageNumber(), organizationIdentity.getPageSize());
        Page<OrganizationProjection> organizationEntities = organizationEntityRepository.findAllOrganization(pageRequest);
        log.info("Found organizations in db: {}", organizationEntities);
        return organizationEntities.map(organizationIdentityMapper::mapProjecttionToOrganizationIdentity);
    }

    @Override
    public Page<OrganizationIdentity> viewAllOrganizationByStatus(OrganizationIdentity organizationIdentity, List<String> activationStatuses) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity, OrganizationMessages.ORGANIZATION_TYPE_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(activationStatuses, OrganizationMessages.ORGANIZATION_STATUS_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validatePageSize(organizationIdentity.getPageSize());
        MeedlValidator.validatePageNumber(organizationIdentity.getPageNumber());
        Pageable pageRequest = PageRequest.of(organizationIdentity.getPageNumber(), organizationIdentity.getPageSize(), Sort.by(Sort.Direction.DESC, "invitedDate"));

        log.info("List of statuses {}", activationStatuses);
        Page<OrganizationProjection> organizationEntities= organizationEntityRepository.findAllByStatus(activationStatuses,pageRequest);
        log.info("Organization entities {}", organizationEntities.stream().peek(or -> log.info("Each org invited date {}", or.getInvitedDate())).toList());
        return organizationEntities.map(organizationIdentityMapper::mapProjecttionToOrganizationIdentity);
    }

    @Override
    public List<ServiceOffering> findServiceOfferingById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, OrganizationMessages.INVALID_SERVICE_OFFERING_ID.getMessage());
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings =
                organizationServiceOfferingRepository.findAllByOrganizationId(id);
        if (organizationServiceOfferings.isEmpty()){
            log.info("No service offerings found for organization with id: {}", id);
            throw new IdentityException("Service offering not found");
        }
        log.info("Found service offerings in DB with size: {}", organizationServiceOfferings.size());
        return organizationIdentityMapper.toServiceOfferings(organizationServiceOfferings);
    }

    @Override
    public Optional<OrganizationIdentity> findByOrganizationId(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, INVALID_ORGANIZATION_ID.getMessage());
        Optional<OrganizationEntity> organizationEntity = organizationEntityRepository.findById(organizationId);
        if (organizationEntity.isEmpty()) return Optional.empty();
        log.info("Organization entity retrieved from DB: {}", organizationEntity.get());
        OrganizationIdentity organizationIdentity = organizationIdentityMapper.toOrganizationIdentity(organizationEntity.get());
        log.info("Mapped Organization identity: {}", organizationEntity.get());
        return Optional.of(organizationIdentity);
    }

    @Override
    public List<OrganizationServiceOffering> findOrganizationServiceOfferingsByOrganizationId(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, INVALID_ORGANIZATION_ID.getMessage());
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings =
                organizationServiceOfferingRepository.findAllByOrganizationId(organizationId);
        return organizationIdentityMapper.toOrganizationServiceOfferings(organizationServiceOfferings);
    }

    @Override
    public List<ServiceOffering> getServiceOfferings(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, INVALID_ORGANIZATION_ID.getMessage());
        List<ServiceOfferingEntity> serviceOfferingEntities = serviceOfferEntityRepository.findAllByOrganizationId(organizationId);
        log.info("Retrieved service offering entities: {}", serviceOfferingEntities);
        List<ServiceOffering> serviceOfferings = serviceOfferingEntities
                .stream()
                .map(organizationIdentityMapper::toServiceOffering)
                .toList();
        log.info("Service offerings: {}", serviceOfferings);
        return serviceOfferings;
    }

    @Override
    public void deleteOrganizationServiceOffering(String organizationServiceOfferingId) throws MeedlException {
        MeedlValidator.validateUUID(organizationServiceOfferingId, OrganizationMessages.INVALID_SERVICE_OFFERING_ID.getMessage());
        Optional<OrganizationServiceOfferingEntity> organizationServiceOffering =
                organizationServiceOfferingRepository.findById(organizationServiceOfferingId);
        if (organizationServiceOffering.isPresent()) {
            log.info("Found organization service offering: {}", organizationServiceOffering.get());
            organizationServiceOfferingRepository.deleteById(organizationServiceOffering.get().getId());
            log.info("Deleted organization service offering: {}", organizationServiceOffering.get());
        }
    }

    @Override
    public void deleteServiceOffering(String serviceOfferingId) throws MeedlException {
        MeedlValidator.validateUUID(serviceOfferingId, OrganizationMessages.INVALID_SERVICE_OFFERING_ID.getMessage());
        Optional<ServiceOfferingEntity> serviceOfferingEntity = serviceOfferEntityRepository.
                findById(serviceOfferingId);
        if (serviceOfferingEntity.isPresent()) {
            log.info("Found service offering: {}", serviceOfferingEntity.get());
            serviceOfferEntityRepository.deleteById(serviceOfferingEntity.get().getId());
            log.info("Deleted service offering: {}", serviceOfferingEntity.get());
        }
    }

    @Override
    public Page<OrganizationIdentity> findAllWithLoanMetrics(LoanType loanType, int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanType,"Loan type cannot be empty");
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<OrganizationProjection> organizations = organizationEntityRepository.findAllWithLoanMetrics(loanType.name(),pageRequest);
        if (CollectionUtils.isEmpty(Collections.singleton(organizations))) {
            return Page.empty();
        }
        return organizations.map(organizationIdentityMapper::projectionToOrganizationIdentity);
    }

    @Override
    public Page<OrganizationIdentity> findByName(String name,ActivationStatus activationStatus,int pageSize, int pageNumber) throws MeedlException {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("invitedDate").descending());
        log.info("Searching for organizations with name {}", name);
        Page<OrganizationEntity> organizationEntities =
                organizationEntityRepository.findByNameContainingIgnoreCaseAndStatus(name.trim(),activationStatus,pageRequest);
        log.info("Found {} organizations", organizationEntities);
        return organizationEntities.map(organizationIdentityMapper::toOrganizationIdentity);
    }

    @Override
    public Optional<OrganizationIdentity> findOrganizationByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, OrganizationMessages.ORGANIZATION_NAME_IS_REQUIRED.getMessage());
        Optional<OrganizationEntity> foundOrganization = organizationEntityRepository.findByName(name);
        return foundOrganization.map(organizationIdentityMapper::toOrganizationIdentity);
    }

    @Override
    public void updateNumberOfCohortInOrganization(String organizationId) throws MeedlException {
        OrganizationIdentity organizationIdentity = findById(organizationId);
        organizationIdentity.setNumberOfCohort(organizationIdentity.getNumberOfCohort() + 1);
        organizationIdentity.setOrganizationEmployees(organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationIdentity.getId()));
    }

    @Override
    public Optional<OrganizationIdentity> findByTin(String tin) throws MeedlException {
        MeedlValidator.validateDataElement(tin, MeedlMessages.TIN_CANNOT_BE_EMPTY.getMessage());
        log.info("Searching for organization with tin {}", tin);
        Optional<OrganizationEntity> organizationEntity = organizationEntityRepository.findByTaxIdentity(tin);
        if (organizationEntity.isEmpty()) return Optional.empty();
        log.info("Found organization: {}", organizationEntity);
        return organizationEntity.map(organizationIdentityMapper::toOrganizationIdentity);
    }

    @Override
    public Page<OrganizationIdentity> findByNameSortingByLoanType(String name, LoanType loanType, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanType,"Loan type cannot be empty");
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<OrganizationProjection> organizations = organizationEntityRepository.searchOrganizationSortingWithLoanType(
                name,loanType.name(),pageRequest);
        if (CollectionUtils.isEmpty(Collections.singleton(organizations))) {
            return Page.empty();
        }
        return organizations.map(organizationIdentityMapper::projectionToOrganizationIdentity);
    }


    @Override
    public List<OrganizationIdentity> findAllOrganization() {
        List<OrganizationEntity> organizationEntities = organizationEntityRepository.findAll();
        return organizationEntities.stream().map(organizationIdentityMapper::toOrganizationIdentity).toList();
    }

    @Override
    public Optional<OrganizationIdentity> findByUserId(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, IdentityMessages.INVALID_USER_ID.getMessage());
        Optional<OrganizationEntity> organizationEntity = organizationEntityRepository.findByUserId(userId);
        log.info("The user id used in finding organization is {}", userId);
        if (organizationEntity.isEmpty()) return Optional.empty();
        log.info("Organization entity retrieved from DB by user id: {}", organizationEntity.get());
        OrganizationIdentity organizationIdentity = organizationIdentityMapper.toOrganizationIdentity(organizationEntity.get());
        log.info("Mapped Organization identity, found with user id: {}", organizationEntity.get());
        return Optional.of(organizationIdentity);
    }
}
