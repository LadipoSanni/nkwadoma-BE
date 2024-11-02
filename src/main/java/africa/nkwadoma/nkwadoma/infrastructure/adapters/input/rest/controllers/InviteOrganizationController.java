package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.OrganizationRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.INVITE_ORGANIZATION_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.INVITE_ORGANIZATION_DESCRIPTION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.INVITE_ORGANIZATION_TITLE;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.UPDATE_ORGANIZATION_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class InviteOrganizationController {
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final OrganizationRestMapper organizationRestMapper;

    @PostMapping("organization/invite")
    @Operation(summary = INVITE_ORGANIZATION_TITLE,description = INVITE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> inviteOrganization(@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestBody @Valid OrganizationRequest inviteOrganizationRequest){
        try{
            UserIdentity userIdentity = getUserIdentity(inviteOrganizationRequest);
            userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
            OrganizationEmployeeIdentity organizationEmployeeIdentity = getOrganizationEmployeeIdentity(userIdentity);
            List<OrganizationEmployeeIdentity> orgEmployee = getOrganizationEmployeeIdentities(organizationEmployeeIdentity);
            OrganizationIdentity organizationIdentity = organizationRestMapper.toOrganizationIdentity(inviteOrganizationRequest);
            organizationIdentity.setOrganizationEmployees(orgEmployee);
            organizationIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
            organizationIdentity = createOrganizationUseCase.inviteOrganization(organizationIdentity);
            InviteOrganizationResponse inviteOrganizationResponse = organizationRestMapper.toInviteOrganizationresponse(organizationIdentity);
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .data(inviteOrganizationResponse)
                    .message(INVITE_ORGANIZATION_SUCCESS)
                    .statusCode(HttpStatus.CREATED.toString())
                    .build();
            return new  ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(new ApiResponse<>( null ,exception.getMessage(), HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }
    @PatchMapping("organization/update")
    @Operation(summary = "Update an existing organization")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') and hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> updateOrganization(@RequestBody @Valid OrganizationRequest organizationRequest,
                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationRestMapper.toOrganizationIdentity(organizationRequest);
        organizationIdentity.setUpdatedBy(meedlUser.getClaim("sub"));
        log.info("Program at controller level: ========>{}", organizationIdentity);
        organizationIdentity = createOrganizationUseCase.updateOrganization(organizationIdentity);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(organizationRestMapper.toOrganizationResponse(organizationIdentity))
                .message(UPDATE_ORGANIZATION_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new  ResponseEntity<>(apiResponse,HttpStatus.CREATED);
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

