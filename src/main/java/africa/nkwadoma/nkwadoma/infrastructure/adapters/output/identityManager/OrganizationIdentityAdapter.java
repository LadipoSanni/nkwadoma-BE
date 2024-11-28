package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateEmail;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;

@Slf4j
@RequiredArgsConstructor
public class OrganizationIdentityAdapter implements OrganizationIdentityOutputPort {
    private final OrganizationEntityRepository organizationEntityRepository;
    private final ServiceOfferEntityRepository serviceOfferEntityRepository;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationServiceOfferingRepository organizationServiceOfferingRepository;

    @Override
    public OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("Organization identity before saving {}", organizationIdentity);
        MeedlValidator.validateObjectInstance(organizationIdentity);
        organizationIdentity.validate();
        MeedlValidator.validateOrganizationUserIdentities(organizationIdentity.getOrganizationEmployees());

        validate(organizationIdentity);
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity.setInvitedDate(LocalDateTime.now());
        organizationEntity = organizationEntityRepository.save(organizationEntity);

        List<ServiceOfferingEntity> serviceOfferingEntities = saveServiceOfferingEntities(organizationIdentity);
        saveOrganizationServiceOfferings(serviceOfferingEntities, organizationEntity);
        log.info("Organization entity saved successfully {}", organizationEntity);
//        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
        List<ServiceOffering> savedServiceOfferings = organizationIdentityMapper.toServiceOfferingEntitiesServiceOfferings(serviceOfferingEntities);
        log.info("Organization entity saved successfully");

        organizationIdentity = organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
        organizationIdentity.setServiceOfferings(savedServiceOfferings);
        return organizationIdentity;
    }
    public void validate(OrganizationIdentity organizationIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationIdentity);
        organizationIdentity.validate();
        MeedlValidator.validateOrganizationUserIdentities(organizationIdentity.getOrganizationEmployees());
    }
    @Override
    public Optional<OrganizationEntity> findByRcNumber(String rcNumber) {
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
        validateEmail(email);
        OrganizationEntity organizationEntity = organizationEntityRepository.findByEmail(email).
                orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND.getMessage()));
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        validateDataElement(id);
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(id).
                orElseThrow(()-> new IdentityException(ORGANIZATION_NOT_FOUND.getMessage()));
        organizationEntityRepository.delete(organizationEntity);
    }

    @Override
    public OrganizationIdentity findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }
    @Override
    public Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException {
        log.info("Searching for all organizations at adapter level.");
        MeedlValidator.validateObjectInstance(organizationIdentity);
        MeedlValidator.validatePageSize(organizationIdentity.getPageSize());
        MeedlValidator.validatePageNumber(organizationIdentity.getPageNumber());
        Pageable pageRequest = PageRequest.of(organizationIdentity.getPageNumber(), organizationIdentity.getPageSize());
        log.info("Page number: {}, page size: {}", organizationIdentity.getPageNumber(), organizationIdentity.getPageSize());
        Page<OrganizationEntity> organizationEntities = organizationEntityRepository.findAll(pageRequest);
        log.info("Found organizations in db: {}", organizationEntities);
         return organizationEntities.map(organizationIdentityMapper::toOrganizationIdentity);
    }

    @Override
    public boolean existsById(String organizationId) {
        return organizationEntityRepository.existsById(organizationId);
    }

    @Override
    public List<ServiceOffering> findServiceOfferingById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings =
                organizationServiceOfferingRepository.findByOrganizationId(id);
        if (organizationServiceOfferings.isEmpty()){
            throw new IdentityException("Service offering not found");
        }
        return organizationIdentityMapper.toServiceOfferings(organizationServiceOfferings);
    }

    @Override
    public List<OrganizationServiceOffering> findOrganizationServiceOfferingsByOrganizationId(String organizationId) throws MeedlException {
        MeedlValidator.validateDataElement(organizationId);
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings =
                organizationServiceOfferingRepository.findByOrganizationId(organizationId);
        log.info("Found org sev offerings in db: {}", organizationServiceOfferings);
        return organizationIdentityMapper.toOrganizationServiceOfferings(organizationServiceOfferings);
    }

    @Override
    public void deleteOrganizationServiceOffering(String organizationServiceOfferingId) throws MeedlException {
        MeedlValidator.validateDataElement(organizationServiceOfferingId);
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
        MeedlValidator.validateDataElement(serviceOfferingId);
        Optional<ServiceOfferingEntity> serviceOfferingEntity = serviceOfferEntityRepository.findById(serviceOfferingId);
        if (serviceOfferingEntity.isPresent()) {
            log.info("Found service offering: {}", serviceOfferingEntity.get());
            serviceOfferEntityRepository.deleteById(serviceOfferingEntity.get().getId());
            log.info("Deleted service offering: {}", serviceOfferingEntity.get());
        }
    }

    @Override
    public List<OrganizationIdentity> findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name);
        log.info("Searching for organizations with name {}", name);
        List<OrganizationEntity> organizationEntities = organizationEntityRepository.findAllByName(name.trim());
        log.info("Found {} organizations", organizationEntities);
        return organizationEntities.stream().map(organizationIdentityMapper::toOrganizationIdentity).toList();
    }

    private OrganizationIdentity saveAndGetUserIdentity(OrganizationIdentity organizationIdentity) {
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }
}
