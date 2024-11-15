package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeReferralResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.LoaneeRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.LOANEE_ADDED_TO_COHORT;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.LOANEE_HAS_BEEN_REFERED;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class LoaneeController {

    private final LoaneeRestMapper loaneeRestMapper;
    private final LoaneeUseCase loaneeUsecase;


    @PostMapping("addLoaneeToCohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> addLoaneeToCohort(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestBody LoaneeRequest loaneeRequest) throws MeedlException {
        Loanee loanee = loaneeRestMapper.toLoanee(loaneeRequest);
        loanee.setCreatedBy(meedlUser.getClaimAsString("sub"));
        loanee.getLoanee().setCreatedBy(loanee.getCreatedBy());
        loanee = loaneeUsecase.addLoaneeToCohort(loanee);
        LoaneeResponse loaneeResponse =
                loaneeRestMapper.toLoaneeResponse(loanee);
        ApiResponse<LoaneeResponse> apiResponse = ApiResponse.<LoaneeResponse>builder()
                .data(loaneeResponse)
                .message(LOANEE_ADDED_TO_COHORT)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @PostMapping("referLoanee/{loaneeId}")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> referLoanee(@PathVariable String loaneeId) throws MeedlException {
        LoanReferral loanReferral = loaneeUsecase.referLoanee(loaneeId);
        LoaneeReferralResponse loaneeReferralResponse =
                loaneeRestMapper.toLoaneeReferralResponse(loanReferral);
        ApiResponse<LoaneeReferralResponse> apiResponse = ApiResponse.<LoaneeReferralResponse>builder()
                .data(loaneeReferralResponse)
                .message(LOANEE_HAS_BEEN_REFERED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

}
