package africa.nkwadoma.nkwadoma.application.ports.input.notification;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;

public interface OrganizationEmployeeEmailUseCase {
    void sendEmail(UserIdentity userIdentity) throws MeedlException;
    void sendForgotPasswordEmail(UserIdentity userIdentity) throws MeedlException;

    void sendDeactivationEmail(OrganizationEmployeeIdentity organizationEmployee, OrganizationIdentity organization, String deactivationReason);
}
