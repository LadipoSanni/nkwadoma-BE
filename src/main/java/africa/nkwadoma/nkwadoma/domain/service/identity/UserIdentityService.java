package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.PasswordHistoryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.PasswordHistory;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_HAS_BEEN_CREATED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_NOT_ACCEPTED;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.validateEmailDomain;
import static africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator.validatePassword;

@Slf4j
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final TokenGeneratorOutputPort tokenGeneratorOutputPort;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryOutputPort passwordHistoryOutputPort;





    @Override
    public UserIdentity inviteColleague(UserIdentity userIdentity) throws MiddlException {
        UserIdentity inviter = userIdentityOutputPort.findById(userIdentity.getCreatedBy());
        validateEmailDomain(userIdentity.getEmail(),inviter.getEmail());
        OrganizationEmployeeIdentity foundEmployee = organizationEmployeeIdentityOutputPort.findByEmployeeId(inviter.getId());
        userIdentity.setRole(inviter.getRole());
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = identityManagerOutPutPort.createUser(userIdentity);
        userIdentityOutputPort.save(userIdentity);

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(foundEmployee.getOrganization());
        organizationEmployeeIdentity.setMiddlUser(userIdentity);
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);

        return userIdentity;
    }

    @Override
    public void createPassword(String token, String password) throws MiddlException {
        validatePassword(password);
        String email = tokenGeneratorOutputPort.decodeJWT(token);
        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(email);

        if (!userIdentity.isEmailVerified() || !userIdentity.isEnabled()){
            userIdentity.setEmailVerified(true);
            userIdentity.setEnabled(true);
            String encodedPassword = passwordEncoder.encode(password);
            userIdentity.setPassword(password);

            List<PasswordHistory> passwordHistories = userIdentity.getPasswordHistories();

            if (passwordHistories == null) {
                passwordHistories = new ArrayList<>();
            }

            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setPassword(password);
            passwordHistory.setMiddlUser(userIdentity.getId());
            passwordHistoryOutputPort.save(passwordHistory);

            passwordHistories.add(passwordHistory);
            userIdentityOutputPort.save(userIdentity);
            identityManagerOutPutPort.createPassword(userIdentity.getEmail(), userIdentity.getPassword());
        }
       else throw new IdentityException(PASSWORD_HAS_BEEN_CREATED.getMessage());
    }

    @Override
    public UserIdentity login(UserIdentity userIdentity)throws MiddlException {
        UserIdentityValidator.validateUserDataElement(userIdentity.getEmail());
        UserIdentityValidator.validateUserDataElement(userIdentity.getPassword());
        return identityManagerOutPutPort.login(userIdentity);
    }

    @Override
    public void changePassword(UserIdentity userIdentity) throws MiddlException {
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

        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setPassword(userIdentity.getPassword());
        passwordHistory.setMiddlUser(userIdentity.getId());
        passwordHistoryOutputPort.save(passwordHistory);

        passwordHistories.add(passwordHistory);


        userIdentityOutputPort.save(userIdentity);
        identityManagerOutPutPort.changePassword(userIdentity);
    }
    private boolean checkNewPasswordMatchLastFive(String newPassword, String userId) throws MiddlException {
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

