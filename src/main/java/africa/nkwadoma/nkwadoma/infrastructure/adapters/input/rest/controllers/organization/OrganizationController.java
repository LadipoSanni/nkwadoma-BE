package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.organization;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.OrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.ViewOrganizationUseCase;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ReferenceDataResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.OrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.OrganizationRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
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
import java.util.stream.Collectors;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {
    private final OrganizationUseCase createOrganizationUseCase;
    private final ViewOrganizationUseCase viewOrganizationUseCase;
    private final OrganizationRestMapper organizationRestMapper;

    @PostMapping("organization/invite")
    @Operation(summary = INVITE_ORGANIZATION_TITLE, description = INVITE_ORGANIZATION_DESCRIPTION)
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN') ")
    public ResponseEntity<ApiResponse<?>> inviteOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestBody @Valid OrganizationRequest inviteOrganizationRequest) throws MeedlException {
        String createdBy = meedlUser.getClaimAsString("sub");
        OrganizationIdentity organizationIdentity = setOrganizationEmployeesInOrganization(inviteOrganizationRequest, createdBy);
        organizationIdentity = createOrganizationUseCase.inviteOrganization(organizationIdentity);
        log.info("Organization identity from service level: {}", organizationIdentity);
        InviteOrganizationResponse inviteOrganizationResponse = organizationRestMapper.toInviteOrganizationresponse(organizationIdentity);
        log.info("Mapped Organization identity from service level: {}", organizationIdentity);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(inviteOrganizationResponse)
                .message(INVITE_ORGANIZATION_SUCCESS)
                .statusCode(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PatchMapping("organization/update")
    @Operation(summary = "Update an existing organization")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateOrganization(@RequestBody @Valid OrganizationUpdateRequest organizationUpdateRequest,
                                                             @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationRestMapper.maptoOrganizationIdentity(organizationUpdateRequest);
        organizationIdentity.setUpdatedBy(meedlUser.getClaim("sub"));
        log.info("Program at controller level: ========>{}", organizationIdentity);
         organizationIdentity = createOrganizationUseCase.updateOrganization(organizationIdentity);
        ApiResponse<Object> apiResponse = ApiResponse.buildApiResponse(organizationRestMapper.toOrganizationResponse(organizationIdentity),
                UPDATE_ORGANIZATION_SUCCESS,
                HttpStatus.CREATED.name());
        return new  ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @GetMapping("organization/search")
    @Operation(summary = "Search for organization(s) by similar or precise name")
     @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
             "or hasRole('MEEDL_ADMIN')" +
             "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
             "or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchOrganizationByName(
                                                                       @RequestParam(name = "name") String name,
                                                                       @RequestParam(name = "status" , required = false) ActivationStatus status,
                                                                       @RequestParam(name = "loanType" , required = false) LoanType loanType,
                                                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                       @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber)
            throws MeedlException {
        OrganizationIdentity organizationIdentity = OrganizationIdentity.builder().name(name).activationStatus(status).loanType(loanType)
                .pageNumber(pageNumber).pageSize(pageSize).build();
        Page<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase.search(organizationIdentity);
        List<OrganizationResponse> organizationResponses =
                organizationIdentities.stream().map(organizationRestMapper::toOrganizationResponse).collect(Collectors.toList());
        PaginatedResponse<OrganizationResponse> paginatedResponse = new PaginatedResponse<>(
                organizationResponses,organizationIdentities.hasNext(),organizationIdentities.getTotalPages(),
                organizationIdentities.getTotalElements() ,pageNumber,pageSize
        );

        log.info("Organization {}", organizationIdentities);
        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.name()).
                data(paginatedResponse).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organizations")
    @Operation(summary = "View all organizations with their loan metrics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found all organizations",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationIdentity.class))
            })
    })
    public ResponseEntity<ApiResponse<?>> viewAllOrganizationWithLoanRequest(@RequestParam()LoanType loanType,
                                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                             @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber
                                                                             ) throws MeedlException {
        Page<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase.viewAllOrganizationsLoanMetrics(loanType,pageSize,pageNumber);
        log.info("Organizations retrieved: {}", organizationIdentities);
        List<OrganizationResponse> organizationResponses =
                organizationIdentities.stream().map(organizationRestMapper::toOrganizationResponse).collect(Collectors.toList());
        PaginatedResponse<OrganizationResponse> paginatedResponse = new PaginatedResponse<>(
                organizationResponses,organizationIdentities.hasNext(),organizationIdentities.getTotalPages(),
                organizationIdentities.getTotalElements() ,pageNumber,pageSize
        );
        log.info("Organization response mapped: {}", organizationResponses);
        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.name()).
                data(paginatedResponse).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organization/details")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('ORGANIZATION_ASSOCIATE')" +
            "or hasRole('ORGANIZATION_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = "View organization details by organization id")
    public ResponseEntity<ApiResponse<?>> viewOrganizationDetails(@RequestParam(required = false) String organizationId,
                                                                  @AuthenticationPrincipal Jwt meedlUser)
            throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        OrganizationIdentity organizationIdentity = viewOrganizationUseCase.viewOrganizationDetails(organizationId, userId);
        log.info("Organization retrieved: {}", organizationIdentity);
        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.name()).
                data(organizationRestMapper.toOrganizationResponse(organizationIdentity)).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("organization")
    @Operation(summary = "View top organization with the highest number of loan requests")
    public ResponseEntity<ApiResponse<?>> viewTopOrganizationByLoanRequest() throws MeedlException {
        OrganizationIdentity organizationIdentity = viewOrganizationUseCase.viewTopOrganizationByLoanRequestCount();
        log.info("Organization identity details: ===> {}", organizationIdentity);
        OrganizationResponse organizationResponse = organizationRestMapper.toOrganizationResponse(organizationIdentity);
        log.info("Organization response: ===> {}", organizationResponse);
        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.name()).
                data(organizationResponse).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }


    @PostMapping("organization/deactivate")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN')")
    @Operation(summary = DEACTIVATE_ORGANIZATION_TITLE, description = DEACTIVATE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> deactivateOrganization(@RequestBody @Valid AccountActivationRequest accountActivationRequest) throws MeedlException {
            createOrganizationUseCase.deactivateOrganization(accountActivationRequest.getId(), accountActivationRequest.getReason());
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .message(ORGANIZATION_DEACTIVATION_SUCCESS)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("organization/reactivate")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN')")
    @Operation(summary = REACTIVATE_ORGANIZATION_TITLE, description = REACTIVATE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> reactivateOrganization(@RequestBody @Valid AccountActivationRequest accountActivationRequest) throws MeedlException {
        createOrganizationUseCase.reactivateOrganization(accountActivationRequest.getId(), accountActivationRequest.getReason());
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .message(ORGANIZATION_REACTIVATION_SUCCESS)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("organization/reference/data")
    @Operation(summary = REFERENCE_DATA_TITLE, description = REFERENCE_DATA_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> referenceData(){
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(new ReferenceDataResponse())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("organization/all")
    @Operation(summary = "View all Organizations", description = "Fetch all organizations ")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllOrganization(@RequestParam int pageNumber, @RequestParam int pageSize)
            throws MeedlException {
        Page<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase
                                                                .viewAllOrganization(OrganizationIdentity.builder()
                                                                .pageNumber(pageNumber)
                                                                .pageSize(pageSize)
                                                                .build());

        return convertAllOrganizationViewedToResponse(pageNumber, pageSize, organizationIdentities);
    }

    @GetMapping("organization/all/status")
    @Operation(summary = "View all Organizations with status", description = "Fetch all organizations with status")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllOrganizationByStatus(@RequestParam int pageNumber, @RequestParam int pageSize, @RequestParam ActivationStatus status)
            throws MeedlException {
        Page<OrganizationIdentity> organizationIdentities = viewOrganizationUseCase
                .viewAllOrganizationByStatus(OrganizationIdentity.builder()
                        .pageNumber(pageNumber)
                        .pageSize(pageSize)
                        .build(), status);
        return convertAllOrganizationViewedToResponse(pageNumber, pageSize, organizationIdentities);
    }

    private ResponseEntity<ApiResponse<?>> convertAllOrganizationViewedToResponse(@RequestParam int pageNumber, @RequestParam int pageSize, Page<OrganizationIdentity> organizationIdentities) {
        List<OrganizationResponse> organizationResponses = organizationIdentities.stream().filter(organizationIdentity -> !organizationIdentity.getName().equalsIgnoreCase("Meedl")).map(organizationRestMapper::toOrganizationResponse).toList();
        PaginatedResponse<OrganizationResponse> response = new PaginatedResponse<>(
                organizationResponses, organizationIdentities.hasNext(),
                organizationIdentities.getTotalPages(), organizationIdentities.getTotalElements(), pageNumber,
                pageSize
        );

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    private OrganizationIdentity setOrganizationEmployeesInOrganization(OrganizationRequest inviteOrganizationRequest, String createdBy) {
        UserIdentity userIdentity = getUserIdentity(inviteOrganizationRequest);
        userIdentity.setCreatedBy(createdBy);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = getOrganizationEmployeeIdentity(userIdentity);
        List<OrganizationEmployeeIdentity> orgEmployee = getOrganizationEmployeeIdentities(organizationEmployeeIdentity);
        OrganizationIdentity organizationIdentity = organizationRestMapper.toOrganizationIdentity(inviteOrganizationRequest);
        organizationIdentity.setOrganizationEmployees(orgEmployee);
        organizationIdentity.setCreatedBy(createdBy);
        return organizationIdentity;
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


    @PostMapping("organization/approve/invite")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> respondToOrganizationInvite(@AuthenticationPrincipal Jwt meedlUser,
                                                                      @RequestBody OrganizationDecisionRequest organizationDecisionRequest) throws MeedlException {

        log.info("request that got in - organization{} == status{}",organizationDecisionRequest.getOrganizationId(),
                organizationDecisionRequest.getActivationStatus());
        String response = createOrganizationUseCase.respondToOrganizationInvite(meedlUser.getClaimAsString("sub"),
                organizationDecisionRequest.getOrganizationId(),
                organizationDecisionRequest.getActivationStatus());
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.toString())
                .message(response)
                .data(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }


    @PostMapping("organization/colleague/invite")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN')  " +
                  " or hasRole('ORGANIZATION_SUPER_ADMIN')  or hasRole('ORGANIZATION_ADMIN') ")
    public ResponseEntity<ApiResponse<?>> inviteColleague(@AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestBody InviteColleagueRequest inviteColleagueRequest) throws MeedlException {
        OrganizationIdentity organizationIdentity =
                organizationRestMapper.mapInviteColleagueRequestToOrganizationIdentity(inviteColleagueRequest);
        log.info("request after mapping {}",organizationIdentity.getUserIdentity());
        organizationIdentity.getUserIdentity().setCreatedBy(meedlUser.getClaimAsString("sub"));
        String response = createOrganizationUseCase.inviteColleague(organizationIdentity);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.toString())
                .message(response)
                .data(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

}



