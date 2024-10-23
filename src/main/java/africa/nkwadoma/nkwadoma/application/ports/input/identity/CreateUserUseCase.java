package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import org.keycloak.representations.*;

public interface CreateUserUseCase {
    UserIdentity inviteColleague(UserIdentity userIdentity) throws MeedlException;
    UserIdentity createPassword(String token,String password) throws MeedlException;
    AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException;
    void changePassword(UserIdentity userIdentity)throws MeedlException;
    void forgotPassword(String email) throws MeedlException;
    UserIdentity reactivateUserAccount(UserIdentity userIdentity) throws MeedlException;
    UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException;

    boolean checkNewPasswordMatchLastFive(UserIdentity userIdentity) throws MeedlException;
}
