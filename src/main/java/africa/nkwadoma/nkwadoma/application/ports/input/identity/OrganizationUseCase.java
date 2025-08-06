package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;

public interface OrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity)throws MeedlException;

    OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException;

    OrganizationIdentity updateOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;

    OrganizationIdentity reactivateOrganization(String organizationId, String reason) throws MeedlException;

    void updateOrganizationStatus(OrganizationIdentity organizationIdentity) throws MeedlException;

    String respondToOrganizationInvite(String actorId,String organizationId, ActivationStatus activationStatus) throws MeedlException;
}
