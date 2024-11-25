package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.IdentityVerificationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.IdentityVerificationFailureRecordRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.IdentityVerificationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.IdentityVerificationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.IdentityVerificationRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.IDENTITY_VERIFICATION;

@Slf4j
@RestController
@RequestMapping(BASE_URL+IDENTITY_VERIFICATION)
@RequiredArgsConstructor
public class IdentityVerificationController {
    private final IdentityVerificationUseCase identityVerificationUseCase;
    private final IdentityVerificationRestMapper identityVerificationMapper;
    @PostMapping("/token/verify")
    public ResponseEntity<ApiResponse<?>> isUserIdentityVerified(@RequestParam @Valid String token) throws MeedlException, IdentityVerificationException {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(identityVerificationUseCase.isIdentityVerified(token))
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("/identity/verify")
    public ResponseEntity<ApiResponse<?>> verifyIdentity(@RequestBody @Valid IdentityVerificationRequest identityVerificationRequest) throws MeedlException, IdentityVerificationException {
        IdentityVerification identityVerification = identityVerificationMapper.toIdentityVerification(identityVerificationRequest);
        identityVerification = identityVerificationUseCase.verifyIdentity(identityVerification);
        IdentityVerificationResponse identityVerificationResponse = identityVerificationMapper.toIdentityVerificationResponse(identityVerification);
        return ResponseEntity.ok(ApiResponse.<IdentityVerificationResponse>builder()
                .data(identityVerificationResponse)
                .statusCode(HttpStatus.OK.name()).build());
    }
    @PostMapping("/failure-record/create")
    public ResponseEntity<ApiResponse<?>> identityVerificationFailed(@RequestBody @Valid IdentityVerificationFailureRecordRequest identityVerificationFailureRecordRequest) throws IdentityVerificationException {
        IdentityVerificationFailureRecord identityVerificationFailureRecord = identityVerificationMapper.toIdentityVerificationFailureRecord(identityVerificationFailureRecordRequest);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .data(identityVerificationUseCase.createIdentityVerificationFailureRecord(identityVerificationFailureRecord))
                .statusCode(HttpStatus.OK.name()).build());
    }

}
