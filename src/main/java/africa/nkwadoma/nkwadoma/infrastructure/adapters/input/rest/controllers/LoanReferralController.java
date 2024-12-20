package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class LoanReferralController {
    private final RespondToLoanReferralUseCase respondToLoanReferralUseCase;
    private final ViewLoanReferralsUseCase viewLoanReferralsUseCase;
    private final LoanReferralRestMapper loanReferralRestMapper;

    @PostMapping("loan-referrals/respond")
    public ResponseEntity<ApiResponse<?>> respondToLoanReferral
            (@RequestBody LoanReferralResponseRequest request) throws MeedlException {
        LoanReferral referral = new LoanReferral();
        referral.setId(request.getId());
        referral.setLoanReferralStatus(request.getLoanReferralStatus());
        referral.setReasonForDeclining(request.getReason());
        log.info("Loan referral model: {}", referral);
        referral = respondToLoanReferralUseCase.respondToLoanReferral(referral);
        LoanReferralResponse loanReferralResponse = loanReferralRestMapper.toLoanReferralResponse(referral);
        log.info("Loan referral response: {}", loanReferralResponse);
        ApiResponse<LoanReferralResponse> apiResponse = ApiResponse.<LoanReferralResponse>builder().
                data(loanReferralResponse).
                message(SuccessMessages.LOAN_REFERRAL_UPDATED_SUCCESSFULLY).
                statusCode(HttpStatus.OK.name()).build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("loan-referral")
    public ResponseEntity<ApiResponse<?>> viewLoanReferral(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        LoanReferral loanReferral = loanReferralRestMapper.toLoanReferral(meedlUser.getClaimAsString("sub"));
        LoanReferral foundLoanReferral = viewLoanReferralsUseCase.viewLoanReferral(loanReferral);
        log.info("Found loan referral: {}", foundLoanReferral);
        LoanReferralResponse loanReferralResponse = loanReferralRestMapper.toLoanReferralResponse(foundLoanReferral);
        log.info("Mapped Loan referral response: {}", loanReferralResponse);
        ApiResponse<LoanReferralResponse> apiResponse = ApiResponse.<LoanReferralResponse>builder()
                .data(loanReferralResponse)
                .message(SuccessMessages.LOAN_REFERRAL_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
