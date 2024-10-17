package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.keycloak.representations.*;

public interface CreateUserUseCase {
    UserIdentity inviteColleague(UserIdentity userIdentity) throws MeedlException;
    UserIdentity createPassword(String token,String password) throws MeedlException;
    AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException;
    void changePassword(UserIdentity userIdentity)throws MeedlException;
    void resetPassword(String email, String password) throws MeedlException;
    UserIdentity enableAccount(UserIdentity userIdentity) throws MeedlException;
    UserIdentity disableAccount(UserIdentity userIdentity) throws MeedlException;
    UserIdentity forgotPassword(String email) throws MeedlException;
}
