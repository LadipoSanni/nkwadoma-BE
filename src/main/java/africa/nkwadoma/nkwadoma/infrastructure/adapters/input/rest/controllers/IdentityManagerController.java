package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.keycloak.representations.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IdentityManagerController {
    private final UserUseCase userUseCase;
    private final OrganizationUseCase createOrganizationUseCase;
    private final FinancierUseCase financierUseCase;
    private final IdentityMapper identityMapper;
    private final LoaneeUseCase loaneeUseCase;

    @PostMapping("auth/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(@RequestBody @Valid LoginRequest loginRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toLoginUserIdentity(loginRequest);
        AccessTokenResponse tokenResponse = userUseCase.login(userIdentity);
        return ResponseEntity.ok(ApiResponse.<AccessTokenResponse>builder().
                data(tokenResponse).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build()
        );
    }

    @PostMapping("auth/refresh-token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder().refreshToken(refreshTokenRequest.getRefreshToken()).build();
        AccessTokenResponse refreshedTokenResponse = userUseCase.refreshToken(userIdentity);
        return ResponseEntity.ok(ApiResponse.<AccessTokenResponse>builder().
                data(refreshedTokenResponse).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build()
        );
    }

    @PostMapping("auth/logout")
    public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal Jwt meedlUser, HttpServletRequest httpServletRequest) throws MeedlException {
        String accessToken = httpServletRequest.getHeader("Authorization").substring(7);
        UserIdentity userIdentity =  UserIdentity.builder().id(meedlUser.getClaimAsString("sub")).build();
        userIdentity.setAccessToken(accessToken);
        userUseCase.logout(userIdentity);
        return ResponseEntity.ok(ApiResponse.<String>builder().
                message(ControllerConstant.LOGOUT_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build()
        );
    }

    @PostMapping("auth/password/create")
    public ResponseEntity<ApiResponse<?>> createPassword(@RequestBody @Valid PasswordCreateRequest passwordCreateRequest) throws MeedlException {
        log.info("got the request to create password {}", passwordCreateRequest.getPassword());
        UserIdentity userIdentity = identityMapper.toPasswordCreateRequest(passwordCreateRequest);
        userIdentity = userUseCase.createPassword(userIdentity.getEmail(), userIdentity.getPassword());
        if (ObjectUtils.isNotEmpty(userIdentity) && ObjectUtils.isNotEmpty(userIdentity.getRole())){
            activateUser(userIdentity);
        }
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                data(userIdentity).
                message(ControllerConstant.PASSWORD_CREATED_SUCCESSFULLY.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }

    private void activateUser(UserIdentity userIdentity) throws MeedlException {
        if (
                userIdentity.getRole() == IdentityRole.ORGANIZATION_ADMIN ||
                userIdentity.getRole() == IdentityRole.PORTFOLIO_MANAGER
        ) {
            activateOrganizationAndAdmin(userIdentity);
        }else if(userIdentity.getRole() == IdentityRole.FINANCIER){
            activateFinancier(userIdentity);
        } else if (userIdentity.getRole() == IdentityRole.LOANEE) {
            activateLoanee(userIdentity);
        }
    }

    private void activateLoanee(UserIdentity userIdentity) {
        log.info("Started updating loanee status");
        Loanee loanee = Loanee.builder().userIdentity(userIdentity).build();
        loaneeUseCase.updateLoaneeStatus(loanee);
    }

    private void activateFinancier(UserIdentity userIdentity) {
        log.info("Started updating financier status");
        Financier financier = Financier.builder().userIdentity(userIdentity).build();
        financierUseCase.updateFinancierStatus(financier);
    }

    private void activateOrganizationAndAdmin(UserIdentity userIdentity) throws MeedlException {
        log.info("Started updating employee status");
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setUserIdentity(userIdentity);
        createOrganizationUseCase.updateOrganizationStatus(organizationIdentity);
    }


    @PostMapping("auth/password/forgotPassword/{email}")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@PathVariable String email) throws MeedlException {
        log.info("got the request {}", email);
        userUseCase.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.<String>builder().
                message("Please check your email to create new password. "+email).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/password/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody @Valid PasswordCreateRequest passwordCreateRequest) throws MeedlException {
        userUseCase.resetPassword(passwordCreateRequest.getToken(), passwordCreateRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                message(ControllerConstant.PASSWORD_RESET_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/manageMFA")
    public ResponseEntity<ApiResponse<?>> manageMFA(@AuthenticationPrincipal Jwt meedlUser, @RequestBody MFARequest mfaRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.map(meedlUser.getClaimAsString("sub"), mfaRequest);
        String response = userUseCase.manageMFA(userIdentity);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                message(response).
                statusCode(HttpStatus.OK.name()).build());
    }
    @GetMapping("auth/userDetail")
    public ResponseEntity<ApiResponse<UserIdentityResponse>> viewUserDetail(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder().id(meedlUser.getClaimAsString("sub")).build();
        UserIdentity userIdentityFound = userUseCase.viewUserDetail(userIdentity);
        UserIdentityResponse userIdentityResponse = identityMapper.toUserIdentityResponse(userIdentityFound);
        userIdentityResponse.setAdditionalDetailsCompleted(userIdentityFound.isAdditionalDetailsCompleted());
        return ResponseEntity.ok(ApiResponse.<UserIdentityResponse>builder()
                .data(userIdentityResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/user/reactivate")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> reactivateUser(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestBody AccountActivationRequest accountActivationRequest) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder()
                .reactivationReason(accountActivationRequest.getReason())
                .id(accountActivationRequest.getId())
                .build();
        userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
        log.info("The user id of user performing the reactivation: {}",meedlUser.getClaimAsString("sub"));
        UserIdentity createdUserIdentity = userUseCase.reactivateUserAccount(userIdentity);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                data(createdUserIdentity).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }

    @PostMapping("auth/user/deactivate")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('MEEDL_ADMIN')" +
            "or hasRole('PORTFOLIO_MANAGER')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> deactivateUser(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestBody AccountActivationRequest accountActivationRequest) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder()
                .deactivationReason(accountActivationRequest.getReason())
                .id(accountActivationRequest.getId())
                .createdBy(meedlUser.getClaimAsString("sub"))
                .build();
        log.info("The user id of user performing the deactivation: {}",meedlUser.getClaimAsString("sub"));
        UserIdentity createdUserIdentity = userUseCase.deactivateUserAccount(userIdentity);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                data(createdUserIdentity).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/user/assign/role")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " + "or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> assignRole(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestParam String userId,
                                                         @RequestParam IdentityRole identityRole) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder()
                .id(userId)
                .role(identityRole)
                .createdBy(meedlUser.getClaimAsString("sub"))
                .build();
        log.info("The user id {} of user assigning role to user with id : {}",meedlUser.getClaimAsString("sub"), userId);
        UserIdentity createdUserIdentity = userUseCase.assignRole(userIdentity);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder()
                .data(createdUserIdentity)
                .message(ControllerConstant.ROLE_ASSIGNED_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/password/change")
    public ResponseEntity<ApiResponse<?>> changePassword(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestBody PasswordChangeRequest passwordChangeRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toUserIdentity(passwordChangeRequest);
        userIdentity.setId(meedlUser.getClaimAsString("sub"));
        userIdentity.setEmail(meedlUser.getClaimAsString("email"));
        log.info("The user changing the password : {} ",meedlUser.getClaimAsString("sub"));
        userUseCase.changePassword(userIdentity);
        return ResponseEntity.ok(ApiResponse.<String>builder().
                data("Password changed successfully.").message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("user/upload/image")
    public ResponseEntity<ApiResponse<?>> uploadImage(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestParam String imageUrl) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder().id(meedlUser.getClaimAsString("sub")).image(imageUrl).build();
        log.info("The user updating image: {} ",meedlUser.getClaimAsString("sub"));
        userUseCase.uploadImage(userIdentity);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Image uploaded successfully.")
                .statusCode(HttpStatus.OK.name()).build());
    }
}
