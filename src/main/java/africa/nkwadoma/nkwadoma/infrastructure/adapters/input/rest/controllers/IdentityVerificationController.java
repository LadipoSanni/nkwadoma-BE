package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.IdentityVerificationFailureRecordRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.IdentityVerificationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.IdentityVerificationRestMapper;
//import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.IDENTITY_VERIFICATION;

@Slf4j
@RestController
@RequestMapping(IDENTITY_VERIFICATION)
@RequiredArgsConstructor
public class IdentityVerificationController {
    private final IdentityVerificationUseCase identityVerificationUseCase;
    private final IdentityVerificationRestMapper identityVerificationMapper;
//    @PostMapping("/is-verified")
    public ResponseEntity<ApiResponse<?>> isUserIdentityVerified(@RequestParam
                                                                 @NotBlank(message = "Loan referral id is required")
                                                                 String loanReferralId) throws MeedlException {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(identityVerificationUseCase.verifyIdentity(loanReferralId))
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyIdentity(@AuthenticationPrincipal Jwt meedlUser,@RequestBody @Valid IdentityVerificationRequest identityVerificationRequest) throws MeedlException {
        IdentityVerification identityVerification = identityVerificationMapper.toIdentityVerification(identityVerificationRequest);
        String response = identityVerificationUseCase.verifyIdentity(meedlUser.getClaimAsString("sub"),identityVerification);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(response)
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("/failure-record/create")
    public ResponseEntity<ApiResponse<?>> identityVerificationFailed(@RequestBody @Valid IdentityVerificationFailureRecordRequest identityVerificationFailureRecordRequest) throws MeedlException {
        IdentityVerificationFailureRecord identityVerificationFailureRecord = identityVerificationMapper.toIdentityVerificationFailureRecord(identityVerificationFailureRecordRequest);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(identityVerificationUseCase.createIdentityVerificationFailureRecord(identityVerificationFailureRecord))
                .statusCode(HttpStatus.OK.name()).build());
    }

}
