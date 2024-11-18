package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;

public interface CreateOrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;

    OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException;

    OrganizationIdentity updateOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;
}
