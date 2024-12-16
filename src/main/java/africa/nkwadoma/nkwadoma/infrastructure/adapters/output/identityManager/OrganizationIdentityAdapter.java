package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMAIL_NOT_FOUND;

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
        MeedlValidator.validateObjectInstance(organizationIdentity);
        organizationIdentity.validate();
        MeedlValidator.validateOrganizationUserIdentities(organizationIdentity.getOrganizationEmployees());

        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity.setInvitedDate(LocalDateTime.now());
        organizationEntity.setStatus(ActivationStatus.INVITED);
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
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(id)
                .orElseThrow(()-> new IdentityException(ORGANIZATION_NOT_FOUND.getMessage()));
        List<Program> programs = programOutputPort.findAllProgramsByOrganizationId(organizationEntity.getId());
        programs.forEach(program -> {
            try {
                programOutputPort.deleteProgram(program.getId());
            } catch (MeedlException e) {
                log.error("Error deleting program with id {}, while attempting to delete organization with id {}. Error message : {}", program, organizationEntity.getId(), e.getMessage());
            }
        });
        organizationEntityRepository.delete(organizationEntity);
    }

    @Override
    public OrganizationIdentity findById(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(organizationId)
                .orElseThrow(()-> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        OrganizationIdentity organizationIdentity = organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
        organizationIdentity.setServiceOfferings(getServiceOfferings(organizationIdentity));
        return organizationIdentity;
    }
    @Override
    public Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("Searching for all organizations at adapter level.");
        MeedlValidator.validateObjectInstance(organizationIdentity);
        MeedlValidator.validatePageSize(organizationIdentity.getPageSize());
        MeedlValidator.validatePageNumber(organizationIdentity.getPageNumber());
        Pageable pageRequest = PageRequest.of(organizationIdentity.getPageNumber(), organizationIdentity.getPageSize(), Sort.by(Sort.Direction.ASC, "invitedDate"));
        log.info("Page number: {}, page size: {}", organizationIdentity.getPageNumber(), organizationIdentity.getPageSize());
        Page<OrganizationEntity> organizationEntities = organizationEntityRepository.findAll(pageRequest);
        log.info("Found organizations in db: {}", organizationEntities);
        return organizationEntities.map(organizationIdentityMapper::toOrganizationIdentity);
    }

    @Override
    public boolean existsById(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        return organizationEntityRepository.existsById(organizationId);
    }

    @Override
    public List<ServiceOffering> findServiceOfferingById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, OrganizationMessages.INVALID_SERVICE_OFFERING_ID.getMessage());
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings =
                organizationServiceOfferingRepository.findByOrganizationId(id);
        if (organizationServiceOfferings.isEmpty()){
            throw new IdentityException("Service offering not found");
        }
        return organizationIdentityMapper.toServiceOfferings(organizationServiceOfferings);
    }

    @Override
    public List<OrganizationServiceOffering> findOrganizationServiceOfferingsByOrganizationId(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings =
                organizationServiceOfferingRepository.findByOrganizationId(organizationId);
        log.info("Found org service offerings in DB: {}", organizationServiceOfferings);
        return organizationIdentityMapper.toOrganizationServiceOfferings(organizationServiceOfferings);
    }

    @Override
    public List<ServiceOffering> getServiceOfferings(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity);
        MeedlValidator.validateUUID(organizationIdentity.getId());
        return findOrganizationServiceOfferingsByOrganizationId(organizationIdentity.getId())
                .stream()
                .map(OrganizationServiceOffering::getServiceOffering)
                .toList();
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
    public List<OrganizationIdentity> findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, "Organization name is required");
        log.info("Searching for organizations with name {}", name);
        List<OrganizationEntity> organizationEntities = organizationEntityRepository.findByNameContainingIgnoreCase(name.trim());
        log.info("Found {} organizations", organizationEntities);
        return organizationEntities.stream().map(organizationIdentityMapper::toOrganizationIdentity).toList();
    }
    @Override
    public void updateNumberOfCohortInOrganization(String organizationId) throws MeedlException {
        OrganizationIdentity organizationIdentity = findById(organizationId);
        organizationIdentity.setNumberOfCohort(organizationIdentity.getNumberOfCohort() + 1);
        organizationIdentity.setOrganizationEmployees(organizationEmployeeIdentityOutputPort.findAllOrganizationEmployees(organizationIdentity.getId()));
        save(organizationIdentity);
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
}
