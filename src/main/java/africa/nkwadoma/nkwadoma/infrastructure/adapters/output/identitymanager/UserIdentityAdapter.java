package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.USER_NOT_FOUND;
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
                userEntityRepository.existsByEmailIgnoreCase(userIdentity.getEmail())) {
            throw new MeedlException("Email already exists");
        }
        log.info("User in adapter before being mapped to save: {}", userIdentity);
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntity = userEntityRepository.save(userEntity);
        userIdentity = userIdentityMapper.toUserIdentity(userEntity);
        userIdentity.setIdentityVerified(userEntity.isIdentityVerified());
        return userIdentity;
    }

    @Override
    public UserIdentity findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, UserMessages.INVALID_USER_ID.getMessage());
        log.info("Find user in the adapter with id {}", id);
        UserEntity userEntity = userEntityRepository.findById(id).orElseThrow(() -> new IdentityException(IdentityMessages.USER_NOT_FOUND.getMessage()));
        log.info("UserEntity found by id before mapping : {}", userEntity);
        UserIdentity userIdentity = userIdentityMapper.toUserIdentity(userEntity);
        userIdentity.setIdentityVerified(userEntity.isIdentityVerified());
        return userIdentity;
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
            throw new IdentityException(UserMessages.USER_IDENTITY_CANNOT_BE_EMPTY.getMessage());
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
        MeedlValidator.validateObjectInstance(identityRole, IdentityMessages.INVALID_ROLE.getMessage());
        List<UserEntity> userEntities = userEntityRepository.findAllByRole(identityRole);
        log.info("Found {} users by Role {} ", userEntities.size(),identityRole );
        return userEntities.stream().map(userIdentityMapper::toUserIdentity).toList();
    }

    @Override
    public List<UserIdentity> findAllByRoles(Set<IdentityRole> roles) throws MeedlException {
        validateRoles(roles);
        List<UserEntity> userEntities = userEntityRepository.findAllByRoles(roles);

        log.info("Found {} back office admins by Role ", userEntities.size());
        return userEntities.stream().map(userIdentityMapper::toUserIdentity).toList();
    }

    @Override
    public boolean checkIfUserExistByEmail(String email) throws MeedlException {
        validateEmail(email);
        return userEntityRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public UserIdentity findMeedlSuperAdmin() {
        UserEntity userEntity = userEntityRepository.findByRole_MeedlSuperAdmin();
        return userIdentityMapper.toUserIdentity(userEntity);
    }

    @Override
    public Optional<UserIdentity> findFinancierSuperAdminByFinancierId(String financierId) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierId, UserMessages.INVALID_USER_ID.getMessage());
        log.info("About to find financier super admin with financier id {}", financierId);
        Optional<UserEntity> userEntity = userEntityRepository.findAllByRoleAndFinancierId(IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN, financierId);
        return userEntity.map(userIdentityMapper::toUserIdentity);
    }

    @Override
    public void changeUserRole(String userId, IdentityRole identityRole) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateObjectInstance(identityRole, "Invalid role provided to change");
        UserIdentity userIdentity = findById(userId);
        userIdentity.setRole(identityRole);
        UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
        userEntityRepository.save(userEntity);
    }

    private void validateRoles(Set<IdentityRole> roles) throws MeedlException {
        MeedlValidator.validateCollection(roles, "Please provide a list of roles for search for.");
        roles.forEach(identityRole -> {
            try {
                MeedlValidator.validateObjectInstance(identityRole, IdentityMessages.INVALID_ROLE.getMessage());
            } catch (MeedlException e) {
                log.error("Identity role invalid in role list validation {}", identityRole);
            }
        });
        log.info("Done validating list of identity roles {}",roles);
    }

    private UserEntity getUserEntityByEmail(String email) throws IdentityException {
        return userEntityRepository.findByEmailIgnoreCase(email).orElseThrow(()-> new IdentityException(EMAIL_NOT_FOUND.getMessage()));
    }

}
