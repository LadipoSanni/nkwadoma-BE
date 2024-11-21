package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class IdentityManagerController {
    private final CreateUserUseCase createUserUseCase;
    private final IdentityVerificationUseCase verificationUseCase;
    private final IdentityMapper identityMapper;

    @PostMapping("auth/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(@RequestBody @Valid LoginRequest loginRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toLoginUserIdentity(loginRequest);
        AccessTokenResponse tokenResponse = createUserUseCase.login(userIdentity);
        return ResponseEntity.ok(ApiResponse.<AccessTokenResponse>builder().
                data(tokenResponse).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build()
        );
    }
    @PostMapping("auth/logout")
    public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal Jwt meedlUser, HttpServletRequest httpServletRequest) throws MeedlException {
        String accessToken = httpServletRequest.getHeader("Authorization").substring(7);
        UserIdentity userIdentity =  UserIdentity.builder().id(meedlUser.getClaimAsString("sub")).build();
        userIdentity.setAccessToken(accessToken);
        createUserUseCase.logout(userIdentity);
        return ResponseEntity.ok(ApiResponse.<String>builder().
                message(ControllerConstant.LOGOUT_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build()
        );
    }
    @PostMapping("auth/colleague/invite")
    public ResponseEntity<ApiResponse<?>> inviteColleague(@AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestBody UserIdentityRequest userIdentityRequest) throws MeedlException {
            UserIdentity userIdentity = identityMapper.toIdentity(userIdentityRequest);
            userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
            log.info("The user id of user inviting a colleague : {} ",meedlUser.getClaimAsString("sub"));
            UserIdentity createdUserIdentity = createUserUseCase.inviteColleague(userIdentity);
            return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                    data(createdUserIdentity).message(ControllerConstant.COLLEAGUE_INVITED.getMessage()).
                    statusCode(HttpStatus.CREATED.name()).build());
    }

    @PostMapping("auth/password/create")
    public ResponseEntity<ApiResponse<?>> createPassword(@RequestBody @Valid PasswordCreateRequest passwordCreateRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toPasswordCreateRequest(passwordCreateRequest);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                data(createUserUseCase.createPassword(userIdentity.getEmail(), userIdentity.getPassword())).
                message(ControllerConstant.PASSWORD_CREATED_SUCCESSFULLY.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/password/forgotPassword")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestParam String email) throws MeedlException {
        createUserUseCase.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.<String>builder().
                message("Please check your email to create new password. "+email).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/password/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody @Valid PasswordCreateRequest passwordCreateRequest) throws MeedlException {
        createUserUseCase.resetPassword(passwordCreateRequest.getToken(), passwordCreateRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                message(ControllerConstant.PASSWORD_RESET_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/identity/confirm/token/verify")
    public ResponseEntity<ApiResponse<?>> isUserIdentityVerified(@RequestParam @Valid String token) throws MeedlException, IdentityVerificationException {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(verificationUseCase.isIdentityVerified(token))
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/identity/verify")
    public ResponseEntity<ApiResponse<?>> isUserIdentityVerified(@RequestBody @Valid IdentityVerificationRequest identityVerificationRequest) throws MeedlException, IdentityVerificationException {
        IdentityVerification identityVerification = identityMapper.toIdentityVerification(identityVerificationRequest);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(verificationUseCase.isIdentityVerified(identityVerification))
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/user/reactivate")
    public ResponseEntity<ApiResponse<?>> reactivateUser(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestBody AccountActivationRequest accountActivationRequest) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder()
                .reactivationReason(accountActivationRequest.getReason())
                .id(accountActivationRequest.getId())
                .build();
        userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
        log.info("The user id of user performing the reactivation: {}",meedlUser.getClaimAsString("sub"));
        UserIdentity createdUserIdentity = createUserUseCase.reactivateUserAccount(userIdentity);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                data(createdUserIdentity).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/user/deactivate")
    public ResponseEntity<ApiResponse<?>> deactivateUser(@AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestBody AccountActivationRequest accountActivationRequest) throws MeedlException {
        UserIdentity userIdentity = UserIdentity.builder()
                .deactivationReason(accountActivationRequest.getReason())
                .id(accountActivationRequest.getId())
                .build();
        userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
        log.info("The user id of user performing the deactivation: {}",meedlUser.getClaimAsString("sub"));
        UserIdentity createdUserIdentity = createUserUseCase.deactivateUserAccount(userIdentity);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                data(createdUserIdentity).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("auth/password/change")
    public ResponseEntity<ApiResponse<?>> changePassword(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestBody UserIdentityRequest userIdentityRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toIdentity(userIdentityRequest);
        userIdentity.setId(meedlUser.getClaimAsString("sub"));
        userIdentity.setEmail(meedlUser.getClaimAsString("email"));
        log.info("The user changing the password : {} and ",meedlUser.getClaimAsString("sub"));
        createUserUseCase.changePassword(userIdentity);
        return ResponseEntity.ok(ApiResponse.<String>builder().
                data("Password change successfully.").message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
}
