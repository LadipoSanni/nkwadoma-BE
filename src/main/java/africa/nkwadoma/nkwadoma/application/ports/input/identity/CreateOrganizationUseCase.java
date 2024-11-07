package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface CreateOrganizationUseCase {
    OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity)throws MeedlException;

    OrganizationIdentity deactivateOrganization(String organizationId, String reason) throws MeedlException;

}
