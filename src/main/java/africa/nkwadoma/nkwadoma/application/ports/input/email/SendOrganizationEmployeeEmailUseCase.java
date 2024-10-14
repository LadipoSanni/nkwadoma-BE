package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

public interface SendOrganizationEmployeeEmailUseCase {
    void sendEmail(UserIdentity userIdentity) throws MeedlException;
}
