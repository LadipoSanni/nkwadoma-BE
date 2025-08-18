package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import org.springframework.data.domain.*;

public interface ViewOrganizationEmployeesUseCase {
    Page<OrganizationEmployeeIdentity> viewOrganizationEmployees(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    OrganizationEmployeeIdentity viewEmployeeDetail
            (OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    OrganizationEmployeeIdentity viewEmployeeDetails(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    Page<OrganizationEmployeeIdentity> viewAllAdminInOrganization(OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException;

    @Deprecated
    Page<OrganizationEmployeeIdentity> searchAdminInOrganization(String organizationId,String name,int pageSize,int pageNumber) throws MeedlException;

    String respondToColleagueInvitation(String actorId,String organizationEmployeeId, ActivationStatus activationStatus) throws MeedlException;
}
