package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateEmail;

@RequiredArgsConstructor
@Slf4j
public class UserIdentityAdapter implements UserIdentityOutputPort {
    private final UserEntityRepository userEntityRepository;
    private final UserIdentityMapper userIdentityMapper;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort ;

    @Override
    public UserIdentity save(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        userIdentity.validate();
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntity = userEntityRepository.save(userEntity);
        log.info("UserIdentity saved {}", userIdentity);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public UserIdentity findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public void deleteUserById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        log.info("Deleting user {}", id);
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        employeeIdentityOutputPort.deleteEmployee(id);
        userEntityRepository.delete(userEntity);
    }

    @Override
    public UserIdentity findByEmail(String email) throws MeedlException {
        validateEmail(email);
        UserEntity userEntity =
                getUserEntityByEmail(email);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public void deleteUserByEmail(String email) throws MeedlException {
        validateEmail(email);
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntityRepository.delete(userEntity);
    }



    private UserEntity getUserEntityByEmail(String email) throws IdentityException {
        return userEntityRepository.findByEmail(email).orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND.getMessage()));
    }



}
