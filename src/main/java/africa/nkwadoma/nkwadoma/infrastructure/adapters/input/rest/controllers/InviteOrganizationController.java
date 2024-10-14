package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.InviteOrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.InviteOrganizationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.InviteOrganizationRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages.INVALID_OPERATION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.INVITE_ORGANIZATION_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SwaggerUiConstant.INVITE_ORGANIZATION_DESCRIPTION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SwaggerUiConstant.INVITE_ORGANIZATION_TITLE;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class InviteOrganizationController {
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final InviteOrganizationRestMapper inviteOrganizationRestMapper;

    @PostMapping("invite-organization")
    @Operation(summary = INVITE_ORGANIZATION_TITLE,description = INVITE_ORGANIZATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> inviteOrganization(@RequestBody @Valid InviteOrganizationRequest inviteOrganizationRequest){
        try{
            UserIdentity userIdentity = getUserIdentity(inviteOrganizationRequest);
            OrganizationEmployeeIdentity organizationEmployeeIdentity = getOrganizationEmployeeIdentity(userIdentity);
            List<OrganizationEmployeeIdentity> orgEmployee = getOrganizationEmployeeIdentities(organizationEmployeeIdentity);
            OrganizationIdentity organizationIdentity = inviteOrganizationRestMapper.toOrganizationIdentity(inviteOrganizationRequest);
            organizationIdentity.setOrganizationEmployees(orgEmployee);
            organizationIdentity = createOrganizationUseCase.inviteOrganization(organizationIdentity);
            InviteOrganizationResponse inviteOrganizationResponse = inviteOrganizationRestMapper.toInviteOrganizationresponse(organizationIdentity);
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .body(inviteOrganizationResponse)
                    .message(INVITE_ORGANIZATION_SUCCESS)
                    .statusCode(HttpStatus.CREATED.toString())
                    .build();
            return new  ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, exception.getMessage(), HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }

    }

    private static List<OrganizationEmployeeIdentity> getOrganizationEmployeeIdentities(OrganizationEmployeeIdentity organizationEmployeeIdentity) {
        List<OrganizationEmployeeIdentity> orgEmployee = new ArrayList<>();
        orgEmployee.add(organizationEmployeeIdentity);
        return orgEmployee;
    }

    private static OrganizationEmployeeIdentity getOrganizationEmployeeIdentity(UserIdentity userIdentity) {
        return OrganizationEmployeeIdentity.builder()
                .middlUser(userIdentity)
                .build();
    }

    private static UserIdentity getUserIdentity(InviteOrganizationRequest inviteOrganizationRequest) {

        return UserIdentity.builder()
                .firstName(inviteOrganizationRequest.getAdminFirstName())
                .lastName(inviteOrganizationRequest.getAdminLastName())
                .email(inviteOrganizationRequest.getEmail())
                .role(inviteOrganizationRequest.getAdminRole())
                .createdBy(inviteOrganizationRequest.getCreatedBy())
                .build();
    }
}

