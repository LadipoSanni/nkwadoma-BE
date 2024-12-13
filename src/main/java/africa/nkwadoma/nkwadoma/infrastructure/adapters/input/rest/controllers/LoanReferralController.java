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
    private final LoanReferralRestMapper loanReferralRestMapper;

    @PostMapping("loan-referrals/respond")
    public ResponseEntity<ApiResponse<?>> respondToLoanReferral(@RequestBody LoanReferralResponseRequest request,
                                                                @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        LoanReferral referral = loanReferralRestMapper.maptoLoanReferral(request, meedlUser.getClaim("sub"));
        log.info("Loan referral to be responded to: {}", referral);
        referral = respondToLoanReferralUseCase.respondToLoanReferral(referral);
        LoanReferralResponse loanReferralResponse = loanReferralRestMapper.toLoanReferralResponse(referral);
        ApiResponse<LoanReferralResponse> apiResponse = ApiResponse.<LoanReferralResponse>builder().
                data(loanReferralResponse).
                message(SuccessMessages.SUCCESSFUL_RESPONSE).
                statusCode(HttpStatus.OK.name()).build();
        return ResponseEntity.ok(apiResponse);
    }
}
