package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


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
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        UserIdentityValidator.validateUserIdentity(organizationIdentity.getOrganizationEmployees());
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);

        List<ServiceOffering> serviceOfferings = organizationIdentity.getServiceOfferings();
        log.info("Found Service offerings: {}", serviceOfferings);
        List<ServiceOfferingEntity> serviceOfferingEntity = organizationIdentityMapper.toServiceOfferingEntity(serviceOfferings);
        log.info("Mapped Service offerings: {}", serviceOfferingEntity);
        List<ServiceOfferingEntity> serviceOfferingEntities = serviceOfferEntityRepository.saveAll(serviceOfferingEntity);
        log.info("Service offerings saved successfully");
        for (ServiceOfferingEntity foundServiceOffering : serviceOfferingEntities) {
            OrganizationServiceOfferingEntity organizationServiceOfferingEntity =
                    OrganizationServiceOfferingEntity.builder().organizationId(organizationEntity.getId()).
                            serviceOfferingEntity(foundServiceOffering).build();
            organizationServiceOfferingRepository.save(organizationServiceOfferingEntity);
        }
        log.info("Organization entity saved successfully");
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
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
        validateDataElement(id);
        OrganizationEntity organizationEntity = organizationEntityRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }

    @Override
    public boolean existsById(String organizationId) {
        return organizationEntityRepository.existsById(organizationId);
    }

    @Override
    public List<ServiceOffering> findServiceOfferingById(String id) {
        List<OrganizationServiceOfferingEntity> organizationServiceOfferings = organizationServiceOfferingRepository.findByOrganizationId(id);
        log.info("Found organization service offerings in DB: {}", organizationServiceOfferings);
        List<ServiceOffering> serviceOfferings = organizationIdentityMapper.toServiceOfferings(organizationServiceOfferings);
        log.info("Mapped service offerings: {}", serviceOfferings);
        return serviceOfferings;
    }


    private OrganizationIdentity saveAndGetUserIdentity(OrganizationIdentity organizationIdentity) {
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }
}
