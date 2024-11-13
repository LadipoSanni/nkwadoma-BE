package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUsecase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.AllLoaneeInCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.LoaneeRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.LOANEE_ADDED_TO_COHORT;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class LoaneeController {

    private final LoaneeRestMapper loaneeRestMapper;
    private final LoaneeUsecase loaneeUsecase;


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

    @GetMapping("loanee/all")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllLoaneesInCohort(AllLoaneeInCohortRequest allLoaneeInCohortRequest) throws MeedlException {

        Page<Loanee> loanees = loaneeUsecase.viewAllLoaneeInCohort(allLoaneeInCohortRequest.getCohortId(),
                allLoaneeInCohortRequest.getPageSize(),
                allLoaneeInCohortRequest.getPageNumber());
        List<LoaneeResponse> loaneeResponses = loanees.stream()
                .map(loaneeRestMapper::toLoaneeResponse).toList();
        PaginatedResponse<LoaneeResponse> paginatedResponse = new PaginatedResponse<>(
                loaneeResponses,loanees.hasNext(),
                loanees.getTotalPages(),allLoaneeInCohortRequest.getPageNumber(),allLoaneeInCohortRequest.getPageSize()
        );
        ApiResponse<PaginatedResponse<LoaneeResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoaneeResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.toString())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);

    }


}
