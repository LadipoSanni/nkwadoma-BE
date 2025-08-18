package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.organization;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
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

    @GetMapping("view/employee/details")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('PORTFOLIO_MANAGER')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('ORGANIZATION_ADMIN')" +
            "or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<?> viewAllAdminInOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                        @RequestParam(required = false) String employeeId) throws MeedlException {
        OrganizationEmployeeIdentity organizationEmployeeIdentity =
                OrganizationEmployeeIdentity.builder().id(employeeId).meedlUser(UserIdentity.builder().id(meedlUser.getClaimAsString("sub")).build()).build();
        log.info("The organization employee detail with id at the controller {}",employeeId);
        OrganizationEmployeeIdentity foundOrganizationEmployeeIdentity =
                viewOrganizationEmployeesUseCase.viewEmployeeDetail(organizationEmployeeIdentity);

        OrganizationEmployeeResponse organizationEmployeeResponse = organizationEmployeeRestMapper
                .toOrganizationEmployeeResponse(foundOrganizationEmployeeIdentity);

        ApiResponse<OrganizationEmployeeResponse> apiResponse = ApiResponse.
                        <OrganizationEmployeeResponse>builder()
                .data(organizationEmployeeResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
    @GetMapping("view-all/admin")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('PORTFOLIO_MANAGER')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('ORGANIZATION_ADMIN')" +
            "or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<?> viewAllAdminInOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) Set<IdentityRole> identityRoles,
                                                        @RequestParam(required = false) Set<ActivationStatus> activationStatuses,
                                                        @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                                        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) throws MeedlException {
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationEmployeeRestMapper.toOrganizationEmployeeIdentity(meedlUser.getClaimAsString("sub"), name, identityRoles, activationStatuses, pageNumber, pageSize);

        log.info("The organization employee at the controller {}",organizationEmployeeIdentity);
        log.info("Roles {}, activation statuses {}", identityRoles, activationStatuses);
        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities =
                viewOrganizationEmployeesUseCase.viewAllAdminInOrganization(organizationEmployeeIdentity);

        List<OrganizationEmployeeResponse> organizationEmployeeResponses =
                organizationEmployeeIdentities.stream()
                        .map(organizationEmployeeRestMapper::toOrganizationEmployeeResponse).toList();

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


    @Deprecated
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

    @PostMapping("respond/invite/colleague")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> respondToColleagueInvite(@AuthenticationPrincipal Jwt meedlUser,
                                                                   @RequestParam(name = "organizationEmployeeId") String organizationEmployeeId,
                                                                   @RequestParam(name = "decision") ActivationStatus activationStatus) throws MeedlException {
        String response = viewOrganizationEmployeesUseCase.respondToColleagueInvitation(meedlUser.getClaimAsString("sub"),
                organizationEmployeeId,activationStatus);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(response)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

}
