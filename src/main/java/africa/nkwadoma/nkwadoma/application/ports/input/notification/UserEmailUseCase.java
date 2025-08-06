package africa.nkwadoma.nkwadoma.application.ports.input.notification;

import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

public interface UserEmailUseCase {

    void sendDeactivatedUserEmailNotification(UserIdentity userIdentity);

    void sendReactivatedUserEmailNotification(UserIdentity meedlUser);
}
