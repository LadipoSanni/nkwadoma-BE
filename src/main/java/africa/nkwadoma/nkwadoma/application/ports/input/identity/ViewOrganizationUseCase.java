package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface ViewOrganizationUseCase {
    List<OrganizationIdentity> search(String organizationName) throws MeedlException;

    OrganizationIdentity viewOrganizationDetails(String organizationId) throws MeedlException;

    Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;
}
