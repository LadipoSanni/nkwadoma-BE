package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

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
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        userIdentity.validate();
        if (ObjectUtils.isEmpty(userIdentity.getId()) &&
                userEntityRepository.existsByEmail(userIdentity.getEmail())) {
            throw new MeedlException("Email already exists");
        }
        log.info("User in adapter before being mapped to save: {}", userIdentity);
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntity.setIdentityVerified(userIdentity.isIdentityVerified());
        userEntity = userEntityRepository.save(userEntity);
        log.info("UserIdentity saved to database {}", userEntity);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public UserIdentity findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, UserMessages.INVALID_USER_ID.getMessage());
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(IdentityMessages.USER_NOT_FOUND.getMessage()));
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public void deleteUserById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, UserMessages.INVALID_USER_ID.getMessage());
        log.info("Deleting user {}", id);
        employeeIdentityOutputPort.deleteEmployee(id);
        UserEntity userEntity = null;
        try {
            userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(USER_NOT_FOUND.getMessage()));
        } catch (MeedlException meedlException) {
            log.error("Failed to find user with id {} to delete.", id, meedlException);
            throw new MeedlException(UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
        }
        if (userEntity != null) {
            userEntityRepository.delete(userEntity);
        }
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

    @Override
    public UserIdentity findByBvn(String bvn) throws MeedlException {
        MeedlValidator.validateDataElement(bvn, "Bvn is required");
        log.info("Finding user by encrypted bvn");
        UserEntity userEntity = userEntityRepository.findByBvn(bvn);
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public List<UserIdentity> findAllByRole(IdentityRole identityRole) throws MeedlException {
        MeedlValidator.validateObjectInstance(identityRole, IdentityMessages.INVALID_VALID_ROLE.getMessage());
        List<UserEntity> userEntities = userEntityRepository.findAllByRole(identityRole);
        log.info("Found {} users by Role ", userEntities.size());
        return userEntities.stream().map(userIdentityMapper::toUserIdentity).toList();
    }


    private UserEntity getUserEntityByEmail(String email) throws IdentityException {
        return userEntityRepository.findByEmail(email).orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND.getMessage()));
    }



}
