package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.keycloak.representations.*;

public interface UserUseCase {

    UserIdentity createPassword(String token, String password) throws MeedlException;

    AccessTokenResponse login(UserIdentity userIdentity) throws MeedlException;

    AccessTokenResponse refreshToken(UserIdentity userIdentity) throws MeedlException;

    void resetPassword(String token, String password) throws MeedlException;

    void logout(UserIdentity userIdentity) throws MeedlException;

    void changePassword(UserIdentity userIdentity) throws MeedlException;

    void forgotPassword(String email) throws MeedlException;

    UserIdentity reactivateUserAccount(UserIdentity userIdentity) throws MeedlException;

    UserIdentity deactivateUserAccount(UserIdentity userIdentity) throws MeedlException;

    boolean checkNewPasswordMatchLastFive(UserIdentity userIdentity) throws MeedlException;

    UserIdentity viewUserDetail(UserIdentity userIdentity) throws MeedlException;

    String manageMFA(UserIdentity userIdentity) throws MeedlException;

    void uploadImage(UserIdentity userIdentity) throws MeedlException;
    UserIdentity assignRole(UserIdentity userIdentity) throws MeedlException;
}
