package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.PasswordHistoryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.PasswordHistory;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_HAS_BEEN_CREATED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_NOT_ACCEPTED;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.*;

@Slf4j
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryOutputPort passwordHistoryOutputPort;
    private final SendColleagueEmailUseCase sendEmail;
    private final UserIdentityMapper userIdentityMapper;



    @Override
    public UserIdentity inviteColleague(UserIdentity userIdentity) throws MeedlException {
        UserIdentityValidator.validateUserIdentity(userIdentity);
        OrganizationEmployeeIdentity foundEmployee = organizationEmployeeIdentityOutputPort.findByEmployeeId(userIdentity.getCreatedBy().trim());
        validateEmailDomain(userIdentity.getEmail().trim(), foundEmployee.getMiddlUser().getEmail().trim());
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = identityManagerOutPutPort.createUser(userIdentity);
        userIdentityOutputPort.save(userIdentity);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(foundEmployee.getOrganization());
        organizationEmployeeIdentity.setMiddlUser(userIdentity);
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);

        sendEmail.sendColleagueEmail(userIdentity);

        return userIdentity;
    }

    @Override
    public UserIdentity createPassword(String token, String password) throws MeedlException {
        validatePassword(password);
        validateDataElement(token);
        String email = tokenUtils.decodeJWT(token);
        log.info("The email of the user is: {} creating password", email);
        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(email);
        log.info("The user found by the email is: {}", userIdentity);
        if (!userIdentity.isEmailVerified() && !userIdentity.isEnabled()) {
            userIdentity = identityManagerOutPutPort.createPassword(userIdentity.getEmail(), password);
            return userIdentity;
        }
        else throw new MeedlException(PASSWORD_HAS_BEEN_CREATED.getMessage());
    }

    private PasswordHistory getPasswordHistory(String password, UserIdentity userIdentity) {
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setPassword(password);
        passwordHistory.setMiddlUser(userIdentity.getId());
        passwordHistoryOutputPort.save(passwordHistory);
        return passwordHistory;
    }

    @Override
    public AccessTokenResponse login(UserIdentity userIdentity)throws MeedlException {
        UserIdentityValidator.validateDataElement(userIdentity.getEmail());
        UserIdentityValidator.validateDataElement(userIdentity.getPassword());
        return identityManagerOutPutPort.login(userIdentity);
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MeedlException {
        validatePassword(userIdentity.getNewPassword());
        login(userIdentity);
        if(checkNewPasswordMatchLastFive(userIdentity.getNewPassword(), userIdentity.getId())){
            throw new IdentityException(PASSWORD_NOT_ACCEPTED.getMessage());
        }
        userIdentity.setPassword(userIdentity.getNewPassword());
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        List<PasswordHistory> passwordHistories = userIdentity.getPasswordHistories();
        if (passwordHistories == null) {
            passwordHistories = new ArrayList<>();
        }
        PasswordHistory passwordHistory = getPasswordHistory(userIdentity.getPassword(), userIdentity);
        passwordHistories.add(passwordHistory);
        userIdentityOutputPort.save(userIdentity);
        identityManagerOutPutPort.changePassword(userIdentity);
    }

    @Override
    public void resetPassword(String email, String password) throws MeedlException {
        UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
        validatePassword(password);
        identityManagerOutPutPort.createPassword(foundUser.getEmail(),password);
        foundUser.setPassword(password);
        userIdentityOutputPort.save(foundUser);
    }

    @Override
    public UserIdentity reactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity);
        validateDataElement(userIdentity.getId());
        validateDataElement(userIdentity.getReactivationReason());
        userIdentity = userIdentityOutputPort.findById(userIdentity.getId());
        userIdentity = identityManagerOutPutPort.enableUserAccount(userIdentity);
        userIdentityOutputPort.save(userIdentity);
        log.info("User reactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException {
        validateUserIdentity(userIdentity);
        validateDataElement(userIdentity.getEmail());
        validateDataElement(userIdentity.getDeactivationReason());
        userIdentity = identityManagerOutPutPort.disableUserAccount(userIdentity);
        userIdentityOutputPort.save(userIdentity);
        log.info("User deactivated successfully {}", userIdentity.getId());
        return userIdentity;
    }

    @Override
    public UserIdentity forgotPassword(String email) throws MeedlException {
       validateEmail(email);
      return userIdentityOutputPort.findByEmail(email);
    }

    private boolean checkNewPasswordMatchLastFive(String newPassword, String userId) throws MeedlException {
        List<PasswordHistory> passwordHistories = passwordHistoryOutputPort.findByUser(userId);
        int checkCount = Math.min(5, passwordHistories.size());
        for (int index = passwordHistories.size() - 1; index >= passwordHistories.size() - checkCount; index--) {
            if (Objects.equals(passwordHistories.get(index).getPassword(), newPassword)) {
                return true;
            }
        }
        return false;
    }



}

