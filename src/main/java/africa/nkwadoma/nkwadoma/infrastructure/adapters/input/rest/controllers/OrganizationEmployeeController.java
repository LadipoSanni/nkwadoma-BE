package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
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

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "organization/")
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
                        .pageNumber(pageNumber).build();
        return ResponseEntity.ok(new APIResponse<>(SuccessMessages.ORGANIZATION_ADMINS_RETURNED_SUCCESSFULLY,
                paginatedResponse, HttpStatus.OK.toString())
        );
    }

    @GetMapping("search/admin")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<?> searchOrganizationEmployees(@AuthenticationPrincipal Jwt meedlUser, @RequestParam("name") String name) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        List<OrganizationEmployeeIdentity> organizationEmployeeIdentities =
                viewOrganizationEmployeesUseCase.searchOrganizationAdmin(userId,name);
        List<OrganizationEmployeeResponse> organizationEmployeeResponses =
                organizationEmployeeIdentities.stream().map(organizationEmployeeRestMapper::toOrganizationEmployeeResponse).toList();
        APIResponse<List<OrganizationEmployeeResponse>> apiResponse = APIResponse.<List<OrganizationEmployeeResponse>>builder()
                .data(organizationEmployeeResponses)
                .message(SuccessMessages.SUCCESSFUL_RESPONSE)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("view-all/admin")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<?> viewAllAdminInOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                        @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<OrganizationEmployeeIdentity> organizationEmployeeIdentities =
                viewOrganizationEmployeesUseCase.viewAllAdminInOrganization(meedlUser.getClaimAsString("sub"),pageSize,pageNumber);
        List<OrganizationEmployeeResponse> organizationEmployeeResponses =
                organizationEmployeeIdentities.stream().map(organizationEmployeeRestMapper::toOrganizationEmployeeResponse).toList();
        PaginatedResponse<OrganizationEmployeeResponse> paginatedResponse =new PaginatedResponse<>(
                organizationEmployeeResponses,organizationEmployeeIdentities.hasNext(),organizationEmployeeIdentities.getTotalPages(),pageNumber,pageSize
        );
        APIResponse<PaginatedResponse<OrganizationEmployeeResponse>> apiResponse = APIResponse.<PaginatedResponse
                        <OrganizationEmployeeResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
}
