package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.organization;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping( "organization/")
@Slf4j
@RequiredArgsConstructor
public class OrganizationEmployeeController {
    private final ViewOrganizationEmployeesUseCase viewOrganizationEmployeesUseCase;
    private final OrganizationEmployeeRestMapper organizationEmployeeRestMapper;

    @GetMapping("employees/{organizationId}")
    public ResponseEntity<?> viewOrganizationEmployees(@Valid @PathVariable @NotBlank(message = "Organization ID is required") String organizationId,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                       @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber)
            throws MeedlException {
        OrganizationEmployeeIdentity employeeIdentity = new OrganizationEmployeeIdentity(organizationId, pageNumber, pageSize);
        Page<OrganizationEmployeeIdentity> employeeIdentities = viewOrganizationEmployeesUseCase.
                viewOrganizationEmployees(employeeIdentity);
        List<OrganizationEmployeeResponse> employeeResponses = employeeIdentities.
                map(organizationEmployeeRestMapper::toOrganizationEmployeeResponse).getContent();
        PaginatedResponse<OrganizationEmployeeResponse> paginatedResponse =
                PaginatedResponse.<OrganizationEmployeeResponse>builder()
                        .body(employeeResponses)
                        .hasNextPage(employeeIdentities.hasNext())
                        .pageSize(pageSize)
                        .totalPages(employeeIdentities.getTotalPages())
                        .totalElement(employeeIdentities.getTotalElements())
                        .pageNumber(pageNumber).build();
        return ResponseEntity.ok(ApiResponse
                .buildApiResponse(paginatedResponse,
                SuccessMessages.ORGANIZATION_ADMINS_RETURNED_SUCCESSFULLY,
                HttpStatus.OK.toString()));
    }

    @GetMapping("view-all/admin")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('MEEDL_ASSOCIATE')" +
            "or hasRole('PORTFOLIO_MANAGER')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('ORGANIZATION_ADMIN')" +
            "or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<?> viewAllAdminInOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                        @RequestParam Map<String, String> requestParams) throws MeedlException {
        OrganizationEmployeeIdentity  organizationEmployeeIdentity = mapParamsToOrganizationEmployeeIdentity(meedlUser.getClaimAsString("sub"), requestParams);

        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities =
                viewOrganizationEmployeesUseCase.viewAllAdminInOrganization(organizationEmployeeIdentity);

        List<OrganizationEmployeeResponse> organizationEmployeeResponses =
                organizationEmployeeIdentities.stream().map(organizationEmployeeRestMapper::toOrganizationEmployeeResponse).toList();

        PaginatedResponse<OrganizationEmployeeResponse> paginatedResponse =new PaginatedResponse<>(
                organizationEmployeeResponses,
                organizationEmployeeIdentities.hasNext(),
                organizationEmployeeIdentities.getTotalPages(),
                organizationEmployeeIdentities.getTotalElements() ,
                organizationEmployeeIdentity.getPageNumber(),
                organizationEmployeeIdentity.getPageSize()
        );
        ApiResponse<PaginatedResponse<OrganizationEmployeeResponse>> apiResponse = ApiResponse.<PaginatedResponse
                        <OrganizationEmployeeResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @GetMapping("search-admin")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<?> searchAllAdminInOrganization(@RequestParam @NotBlank(message = "Organization id is required") String organizationId,
                                                          @RequestParam @NotBlank(message = "name") String name,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber)throws MeedlException {
        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities =
                viewOrganizationEmployeesUseCase.searchAdminInOrganization(organizationId,name,pageSize,pageNumber);
        List<OrganizationEmployeeResponse> organizationEmployeeResponses =
                organizationEmployeeIdentities.stream().map(organizationEmployeeRestMapper::toOrganizationEmployeeResponse).toList();
        PaginatedResponse<OrganizationEmployeeResponse> paginatedResponse =new PaginatedResponse<>(
                organizationEmployeeResponses,organizationEmployeeIdentities.hasNext(),
                organizationEmployeeIdentities.getTotalPages(), organizationEmployeeIdentities.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<OrganizationEmployeeResponse>> apiResponse = ApiResponse.<PaginatedResponse
                        <OrganizationEmployeeResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    private OrganizationEmployeeIdentity mapParamsToOrganizationEmployeeIdentity(
            String meedlUserId,
            Map<String, String> params) {

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();

        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setId(meedlUserId);
        organizationEmployeeIdentity.setMeedlUser(userIdentity);

        if (params.containsKey("name")) {
            organizationEmployeeIdentity.setName(params.get("name"));
        }

        if (params.containsKey("activationStatus")) {
            Set<ActivationStatus> statuses = Arrays.stream(params.get("activationStatuses").split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(ActivationStatus::valueOf)
                    .collect(Collectors.toSet());
            organizationEmployeeIdentity.setActivationStatuses(statuses);
            organizationEmployeeIdentity.setActivationStatus(statuses.stream().toList().get(0));
        }

        if (params.containsKey("identityRoles")) {
            Set<IdentityRole> roles = Arrays.stream(params.get("identityRoles").split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(IdentityRole::valueOf)
                    .collect(Collectors.toSet());
            organizationEmployeeIdentity.setIdentityRoles(roles);
        }

        if (params.containsKey("organizationId")) {
            organizationEmployeeIdentity.setOrganization(params.get("organizationId"));
        }

        if (params.containsKey("pageSize")) {
            organizationEmployeeIdentity.setPageSize(Integer.parseInt(params.get("pageSize")));
        } else {
            organizationEmployeeIdentity.setPageSize(10);
        }

        if (params.containsKey("pageNumber")) {
            organizationEmployeeIdentity.setPageNumber(Integer.parseInt(params.get("pageNumber")));
        } else {
            organizationEmployeeIdentity.setPageNumber(0);
        }

        return organizationEmployeeIdentity;
    }

}
