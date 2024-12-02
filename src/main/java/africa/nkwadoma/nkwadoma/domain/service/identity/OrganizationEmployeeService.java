package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrganizationEmployeeService implements ViewOrganizationEmployeesUseCase {
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeOutputPort;

    @Override
    public Page<OrganizationEmployeeIdentity> viewOrganizationEmployees
            (OrganizationEmployeeIdentity organizationEmployeeIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(organizationEmployeeIdentity);
        MeedlValidator.validateUUID(organizationEmployeeIdentity.getOrganization());
        MeedlValidator.validatePageNumber(organizationEmployeeIdentity.getPageNumber());
        MeedlValidator.validatePageSize(organizationEmployeeIdentity.getPageSize());
        Page<OrganizationEmployeeIdentity> organizationEmployees = organizationEmployeeOutputPort.
                findAllOrganizationEmployees(
                organizationEmployeeIdentity.getOrganization(),
                organizationEmployeeIdentity.getPageNumber(),
                organizationEmployeeIdentity.getPageSize()
        );
        if (ObjectUtils.isEmpty(organizationEmployees)) {
            throw new IdentityException(IdentityMessages.ORGANIZATION_EMPLOYEE_NOT_FOUND.getMessage());
        }
        return organizationEmployees;
    }
}
