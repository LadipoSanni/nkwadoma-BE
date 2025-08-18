package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.EmailResendUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailResendController {
    @Autowired
    private EmailResendUseCase emailResendUseCase;

    @PostMapping("loanee/refer/email/resend/{email}")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> resendEmail(@PathVariable @Valid String email) throws MeedlException {
        emailResendUseCase.resendReferralEmail(email);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Email sent successfully.").
                statusCode(HttpStatus.OK.name()).build()
        );
    }

}
