package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.ViewOrganizationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.AccountActivationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationUpdateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.APIResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ReferenceDataResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.OrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.OrganizationRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final ViewOrganizationUseCase viewOrganizationUseCase;
    private final OrganizationRestMapper organizationRestMapper;

    @PostMapping("organization/invite")
    @Operation(summary = INVITE_ORGANIZATION_TITLE, description = INVITE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<APIResponse<?>> inviteOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestBody @Valid OrganizationRequest inviteOrganizationRequest) throws MeedlException {
            UserIdentity userIdentity = getUserIdentity(inviteOrganizationRequest);
            userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
            OrganizationEmployeeIdentity organizationEmployeeIdentity = getOrganizationEmployeeIdentity(userIdentity);
            List<OrganizationEmployeeIdentity> orgEmployee = getOrganizationEmployeeIdentities(organizationEmployeeIdentity);
            OrganizationIdentity organizationIdentity = organizationRestMapper.toOrganizationIdentity(inviteOrganizationRequest);
            organizationIdentity.setOrganizationEmployees(orgEmployee);
            organizationIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
            organizationIdentity = createOrganizationUseCase.inviteOrganization(organizationIdentity);
            log.info("Organization identity from service level: {}", organizationIdentity);
            InviteOrganizationResponse inviteOrganizationResponse = organizationRestMapper.toInviteOrganizationresponse(organizationIdentity);
            log.info("Mapped Organization identity from service level: {}", organizationIdentity);
            APIResponse<Object> apiResponse = APIResponse.builder()
                    .data(inviteOrganizationResponse)
                    .message(INVITE_ORGANIZATION_SUCCESS)
                    .statusCode(HttpStatus.CREATED.name())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

    }
    @PatchMapping("organization/update")
    @Operation(summary = "Update an existing organization")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') and hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<APIResponse<?>> updateOrganization(@RequestBody @Valid OrganizationUpdateRequest organizationUpdateRequest,
                                                             @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationRestMapper.maptoOrganizationIdentity(organizationUpdateRequest);
        organizationIdentity.setUpdatedBy(meedlUser.getClaim("sub"));
        log.info("Program at controller level: ========>{}", organizationIdentity);
         organizationIdentity = createOrganizationUseCase.updateOrganization(organizationIdentity);

        APIResponse<Object> apiResponse = APIResponse.builder()
                .data(organizationRestMapper.toOrganizationResponse(organizationIdentity))
                .message(UPDATE_ORGANIZATION_SUCCESS)
                .statusCode(HttpStatus.CREATED.name())
                .build();
        return new  ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @GetMapping("organization/search")
    @Operation(summary = "Search for organization(s) by similar or precise name")
    public ResponseEntity<APIResponse<?>> searchOrganizationByName(@Valid @RequestParam(name = "name") @NotBlank(message = "Organization name is required") String name)
            throws MeedlException {
        List<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase.search(name);
        log.info("Organization {}", organizationIdentities);
        return new ResponseEntity<>(APIResponse.builder().statusCode(HttpStatus.OK.name()).
                data(organizationIdentities.stream().map(organizationRestMapper::toOrganizationResponse).toList()).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organizations")
    @Operation(summary = "View all organizations with their loan requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all organizations",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationIdentity.class))
            })
    })
    public ResponseEntity<APIResponse<?>> viewAllOrganizationWithLoanRequest() throws MeedlException {
        List<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase.viewAllOrganizationsWithLoanRequest();
        log.info("Organizations retrieved: {}", organizationIdentities);
        List<OrganizationResponse> organizationResponses =
                organizationIdentities.stream().map(organizationRestMapper::toOrganizationResponse).toList();
        log.info("Organization response mapped: {}", organizationResponses);
        return new ResponseEntity<>(APIResponse.builder().statusCode(HttpStatus.OK.name()).
                data(organizationResponses).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organization/{id}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = "View organization details by organization id")
    public ResponseEntity<APIResponse<?>> viewOrganizationDetails(@PathVariable @Valid @NotBlank(message = "Organization id is required") String id)
            throws MeedlException {
        OrganizationIdentity organizationIdentity = viewOrganizationUseCase.viewOrganizationDetails(id);
        log.info("Organization {}", organizationIdentity);
        return new ResponseEntity<>(APIResponse.builder().statusCode(HttpStatus.OK.name()).
                data(organizationRestMapper.toOrganizationResponse(organizationIdentity)).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organization")
    @Operation(summary = "View top organization with the highest number of loan requests")
    public ResponseEntity<APIResponse<?>> viewTopOrganizationByLoanRequest() throws MeedlException {
        OrganizationIdentity organizationIdentity = viewOrganizationUseCase.viewTopOrganizationByLoanRequest();
        log.info("Organization identity details: ===> {}", organizationIdentity);
        OrganizationResponse organizationResponse = organizationRestMapper.toOrganizationResponse(organizationIdentity);
        log.info("Organization response: ===> {}", organizationResponse);
        return new ResponseEntity<>(APIResponse.builder().statusCode(HttpStatus.OK.name()).
                data(organizationResponse).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organization/details")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<APIResponse<?>> viewOrganizationDetails(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        String adminId = meedlUser.getClaimAsString("sub");
        OrganizationIdentity organizationIdentity =
                viewOrganizationUseCase.viewOrganizationDetailsByOrganizationAdmin(adminId);
        OrganizationResponse organizationResponse = organizationRestMapper.toOrganizationResponse(organizationIdentity);
        APIResponse<OrganizationResponse> apiResponse = APIResponse.<OrganizationResponse>builder()
                .data(organizationResponse)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @PostMapping("organization/deactivate")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = DEACTIVATE_ORGANIZATION_TITLE, description = DEACTIVATE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<APIResponse<?>> deactivateOrganization(@RequestBody @Valid AccountActivationRequest accountActivationRequest) throws MeedlException {
            createOrganizationUseCase.deactivateOrganization(accountActivationRequest.getId(), accountActivationRequest.getReason());
            APIResponse<Object> apiResponse = APIResponse.builder()
                    .message(ORGANIZATION_DEACTIVATION_SUCCESS)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("organization/reactivate")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = REACTIVATE_ORGANIZATION_TITLE, description = REACTIVATE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<APIResponse<?>> reactivateOrganization(@RequestBody @Valid AccountActivationRequest accountActivationRequest) throws MeedlException {
        createOrganizationUseCase.reactivateOrganization(accountActivationRequest.getId(), accountActivationRequest.getReason());
        APIResponse<Object> apiResponse = APIResponse.builder()
                .message(ORGANIZATION_REACTIVATION_SUCCESS)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("organization/reference/data")
    @Operation(summary = REFERENCE_DATA_TITLE, description = REFERENCE_DATA_DESCRIPTION)
    public ResponseEntity<APIResponse<?>> referenceData(){
        APIResponse<Object> apiResponse = APIResponse.builder()
                .data(new ReferenceDataResponse())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("organization/all")
    @Operation(summary = "View all Organizations", description = "Fetch all organizations ")
    public ResponseEntity<APIResponse<?>> viewAllOrganization(@RequestParam int pageNumber, @RequestParam int pageSize)
            throws MeedlException {
        Page<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase
                                                                .viewAllOrganization(OrganizationIdentity.builder()
                                                                .pageNumber(pageNumber)
                                                                .pageSize(pageSize)
                                                                .build());
        List<OrganizationResponse> organizationResponses = organizationIdentities.stream().map(organizationRestMapper::toOrganizationResponse).toList();
        PaginatedResponse<OrganizationResponse> response = new PaginatedResponse<>(
                organizationResponses, organizationIdentities.hasNext(),
                organizationIdentities.getTotalPages(), pageNumber,
                pageSize
        );
        return new ResponseEntity<>(APIResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    private static List<OrganizationEmployeeIdentity> getOrganizationEmployeeIdentities(OrganizationEmployeeIdentity organizationEmployeeIdentity) {
        List<OrganizationEmployeeIdentity> orgEmployee = new ArrayList<>();
        orgEmployee.add(organizationEmployeeIdentity);
        return orgEmployee;
    }

    private static OrganizationEmployeeIdentity getOrganizationEmployeeIdentity(UserIdentity userIdentity) {
        return OrganizationEmployeeIdentity.builder()
                .meedlUser(userIdentity)
                .build();
    }

    private static UserIdentity getUserIdentity(OrganizationRequest inviteOrganizationRequest) {

        return UserIdentity.builder()
                .firstName(inviteOrganizationRequest.getAdminFirstName())
                .lastName(inviteOrganizationRequest.getAdminLastName())
                .email(inviteOrganizationRequest.getAdminEmail())
                .role(inviteOrganizationRequest.getAdminRole())
                .build();
    }
}



