package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMAIL_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMPTY_INPUT_FIELD_ERROR;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateEmail;

@RequiredArgsConstructor
@Slf4j
public class UserIdentityAdapter implements UserIdentityOutputPort {
    private final UserEntityRepository userEntityRepository;
    private final UserIdentityMapper userIdentityMapper;

    @Override
    public UserIdentity save(UserIdentity userIdentity) throws MeedlException {
        UserIdentityValidator.validateUserIdentity(userIdentity);
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntity = userEntityRepository.save(userEntity);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public UserIdentity findById(String id) throws MeedlException {
        if (StringUtils.isNotEmpty(id)){
            UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
            return userIdentityMapper.toUserIdentity(userEntity);
        }
        throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

    @Override
    public void deleteUserById(String id) throws MeedlException {
        if (StringUtils.isEmpty(id)){
            throw new IdentityException(EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        userEntityRepository.delete(userEntity);
    }

    @Override
    public UserIdentity findByEmail(String email) throws MeedlException {
        validateEmail(email);
        UserEntity userEntity = getUserEntityByEmail(email);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public void deleteUserByEmail(String email) throws MeedlException {
        validateEmail(email);
        UserEntity userEntity = getUserEntityByEmail(email);
        userEntityRepository.delete(userEntity);
    }

    @Override
    public void verifyUser(String actorId) {

    }

    private UserEntity getUserEntityByEmail(String email) throws IdentityException {
        return userEntityRepository.findByEmail(email).orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND.getMessage()));
    }



}
