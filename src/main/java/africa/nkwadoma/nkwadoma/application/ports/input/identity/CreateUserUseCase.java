package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.keycloak.representations.*;

public interface CreateUserUseCase {
    UserIdentity inviteColleague(UserIdentity userIdentity) throws MiddlException;
    void createPassword(String token,String password) throws MiddlException;
    AccessTokenResponse login(UserIdentity userIdentity) throws MiddlException;
    void changePassword(UserIdentity userIdentity)throws MiddlException;
    void resetPassword(String email, String password) throws MiddlException;
    UserIdentity enableAccount(UserIdentity userIdentity) throws MiddlException;
    UserIdentity disableAccount(UserIdentity userIdentity) throws MiddlException;
    UserIdentity forgotPassword(String email) throws MiddlException;
}
