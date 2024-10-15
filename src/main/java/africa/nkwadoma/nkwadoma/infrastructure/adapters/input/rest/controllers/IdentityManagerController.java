package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.*;
import jakarta.validation.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages.INVALID_OPERATION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class IdentityManagerController {
    private static final Logger log = LoggerFactory.getLogger(IdentityManagerController.class);
    private final CreateUserUseCase createUserUseCase;
    private final IdentityMapper identityMapper;

    @PostMapping("auth/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid UserIdentityRequest userIdentityRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toIdentity(userIdentityRequest);
        AccessTokenResponse tokenResponse = createUserUseCase.login(userIdentity);
        return ResponseEntity.ok(ApiResponse.<AccessTokenResponse>builder().
                body(tokenResponse).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                statusCode(HttpStatus.OK.name()).build());

    }

    @PostMapping("auth/colleague/invite")
    public ResponseEntity<ApiResponse<?>> inviteColleague(@AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestBody UserIdentityRequest userIdentityRequest) throws MeedlException {
            UserIdentity userIdentity = identityMapper.toIdentity(userIdentityRequest);
            userIdentity.setCreatedBy(meedlUser.getClaimAsString("sub"));
            log.info("The user id of user inviting a colleague : {}",meedlUser.getClaimAsString("sub"));
            UserIdentity createdUserIdentity = createUserUseCase.inviteColleague(userIdentity);
            return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                    body(createdUserIdentity).message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                    statusCode(HttpStatus.OK.name()).build());
    }

    @PostMapping("auth/password/create")
    public ResponseEntity<ApiResponse<?>> createPassword(@RequestBody @Valid PasswordCreateRequest passwordCreateRequest) throws MeedlException {
        UserIdentity userIdentity = identityMapper.toPasswordCreateRequest(passwordCreateRequest);
        return ResponseEntity.ok(ApiResponse.<UserIdentity>builder().
                body(createUserUseCase.createPassword(userIdentity.getEmail(), userIdentity.getPassword())).
                message(ControllerConstant.PASSWORD_CREATED_SUCCESSFULLY.getMessage()).
                statusCode(HttpStatus.OK.name()).build());
    }
}
