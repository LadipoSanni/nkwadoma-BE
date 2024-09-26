package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.RC_NUMBER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MiddleValidator.validateEmail;
import static africa.nkwadoma.nkwadoma.domain.validation.MiddleValidator.validateUserDataElement;

@Component
@RequiredArgsConstructor
public class OrganizationIdentityAdapter implements OrganizationIdentityOutputPort {
    private final OrganizationEntityRepository organizationEntityRepository;
    private final OrganizationIdentityMapper organizationIdentityMapper;

    @Override
    public OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MiddlException {
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }

    @Override
    public OrganizationIdentity findByEmail(String email) throws MiddlException {
        validateEmail(email);
        OrganizationEntity organizationEntity = organizationEntityRepository.findByEmail(email).orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND));
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }

    @Override
    public void delete(String id) throws MiddlException {
        validateUserDataElement(id);
        OrganizationEntity organizationEntity = organizationEntityRepository.findByOrganizationId(id).orElseThrow(()-> new IdentityException(RC_NUMBER_NOT_FOUND));
        organizationEntityRepository.delete(organizationEntity);
    }



    @Override
    public OrganizationIdentity findById(String id) throws MiddlException {
        validateUserDataElement(id);
        OrganizationEntity organizationEntity = organizationEntityRepository.findByOrganizationId(id).orElseThrow(()-> new IdentityException(RC_NUMBER_NOT_FOUND));
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }

    @Override
    public OrganizationIdentity update(OrganizationIdentity organizationIdentity) throws MiddlException {
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        OrganizationIdentity setExistingUser = setExistingUserDataElement(organizationIdentity);
        return saveAndGetUserIdentity(setExistingUser);
    }

    private OrganizationIdentity setExistingUserDataElement(OrganizationIdentity organizationIdentity) throws MiddlException {
        OrganizationIdentity existingUser = findById(organizationIdentity.getRcNumber());
        existingUser.setEmail(organizationIdentity.getEmail());
        existingUser.setName(organizationIdentity.getName());
        existingUser.setIndustry(organizationIdentity.getIndustry());
        existingUser.setRcNumber(organizationIdentity.getRcNumber());
        existingUser.setTin(organizationIdentity.getTin());
        return existingUser;
    }

    private OrganizationIdentity saveAndGetUserIdentity(OrganizationIdentity organizationIdentity) {
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        organizationEntity = organizationEntityRepository.save(organizationEntity);
        return organizationIdentityMapper.toOrganizationIdentity(organizationEntity);
    }
}
