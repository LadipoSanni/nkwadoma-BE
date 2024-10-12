package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import lombok.RequiredArgsConstructor;


import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.RC_NUMBER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MiddlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateEmail;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;

@RequiredArgsConstructor
public class OrganizationIdentityAdapter implements OrganizationIdentityOutputPort {
    private final OrganizationEntityRepository organizationEntityRepository;
    private final ServiceOfferEntityRepository serviceOfferEntityRepository;
    private final OrganizationIdentityMapper organizationIdentityMapper;

    @Override
    public OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MeedlException {
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        UserIdentityValidator.validateUserIdentity(organizationIdentity.getOrganizationEmployees());
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);

        ServiceOfferingEntity serviceOfferingEntity = organizationEntity.getServiceOfferingEntity();
        serviceOfferEntityRepository.save(serviceOfferingEntity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);
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


    private OrganizationIdentity saveAndGetUserIdentity(OrganizationIdentity organizationIdentity) {
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }
}
