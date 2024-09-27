package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.EMPTY_INPUT_FIELD_ERROR;
import static africa.nkwadoma.nkwadoma.domain.validation.MiddleValidator.validateEmail;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserIdentityAdapter implements UserIdentityOutputPort {
    private final UserEntityRepository userEntityRepository;
    private final UserIdentityMapper userIdentityMapper;

    @Override
    public UserIdentity save(UserIdentity userIdentity) throws MiddlException {
        UserIdentityValidator.validateUserIdentity(userIdentity);
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntity = userEntityRepository.save(userEntity);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public UserIdentity findById(String id) throws MiddlException {
        if (StringUtils.isNotEmpty(id)){
            UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND));
            return userIdentityMapper.toUserIdentity(userEntity);
        }
        throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL);
    }

    @Override
    public void deleteUserById(String id) throws MiddlException {
        if (StringUtils.isEmpty(id)){
            throw new IdentityException(EMPTY_INPUT_FIELD_ERROR);
        }
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND));
        userEntityRepository.delete(userEntity);
    }

    @Override
    public UserIdentity findByEmail(String email) throws MiddlException {
        validateEmail(email);
        UserEntity userEntity = getUserEntityByEmail(email);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public void deleteUserByEmail(String email) throws MiddlException {
        validateEmail(email);
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntityRepository.delete(userEntity);
    }


    private UserIdentity saveAndGetUserIdentity(UserIdentity userIdentity) {
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntity = userEntityRepository.save(userEntity);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    private UserIdentity setExistingUserIdentity(UserIdentity userIdentity) throws MiddlException {
        UserIdentity existingUser = findByEmail(userIdentity.getEmail());
        existingUser.setLastName(userIdentity.getLastName());
        existingUser.setFirstName(userIdentity.getFirstName());
        existingUser.setEmail(userIdentity.getEmail());
        existingUser.setRole(userIdentity.getRole());
        existingUser.setCreatedAt(userIdentity.getCreatedAt());
        existingUser.setCreatedBy(userIdentity.getCreatedBy());
        return existingUser;
    }

    private UserEntity getUserEntityByEmail(String email) throws IdentityException {
        return userEntityRepository.findByEmail(email).orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND));
    }



}
