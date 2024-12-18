package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;

@RequestMapping(BASE_URL + LOAN)
@RequiredArgsConstructor
@RestController
@Slf4j
public class LoanRequestController {
    private final LoanRequestUseCase loanRequestUseCase;
    private final LoanRequestRestMapper loanRequestRestMapper;

    @GetMapping("/loan-requests")
    public ResponseEntity<ApiResponse<?>> viewAllLoanRequests(
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) throws MeedlException {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setPageNumber(pageNumber);
        loanRequest.setPageSize(pageSize);
        Page<LoanRequest> loanRequests = loanRequestUseCase.viewAllLoanRequests(loanRequest);
        log.info("Loan requests: {}", loanRequests.getContent());
        List<LoanRequestResponse> loanRequestResponses = loanRequests.stream().map(loanRequestRestMapper::toLoanRequestResponse).toList();
        log.info("Loan request responses: {}", loanRequestResponses);
        PaginatedResponse<LoanRequestResponse> paginatedResponse = new PaginatedResponse<>(
                loanRequestResponses, loanRequests.hasNext(),
                loanRequests.getTotalPages(), pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanRequestResponse>> apiResponse = ApiResponse.
                <PaginatedResponse<LoanRequestResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.LOAN_REQUESTS_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/loan-requests/{id}")
    public ResponseEntity<ApiResponse<?>> viewLoanRequestDetails(@Valid @PathVariable @NotBlank
            (message = "Loan request ID is required") String id) throws MeedlException {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(id);
        LoanRequest foundLoanRequest = loanRequestUseCase.viewLoanRequestById(loanRequest);
        log.info("Loan request: {}", foundLoanRequest);
        LoanRequestResponse loanRequestResponse = loanRequestRestMapper.toLoanRequestResponse(foundLoanRequest);
        log.info("Mapped Loan request: {}", loanRequestResponse);
        ApiResponse<LoanRequestResponse> apiResponse = ApiResponse.
                <LoanRequestResponse>builder()
                .data(loanRequestResponse)
                .message(SuccessMessages.LOAN_REQUESTS_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/loan-request/response")
    public ResponseEntity<ApiResponse<?>> respondToLoanRequest(@Valid @RequestBody LoanRequestDto loanRequestDto)
            throws MeedlException {
        LoanRequest loanRequest = loanRequestRestMapper.toLoanRequest(loanRequestDto);
        loanRequest = loanRequestUseCase.respondToLoanRequest(loanRequest);
        LoanRequestResponse loanRequestResponse = loanRequestRestMapper.toLoanRequestResponse(loanRequest);
        ApiResponse<LoanRequestResponse> apiResponse = ApiResponse.
                <LoanRequestResponse>builder()
                .data(loanRequestResponse)
                .message(SuccessMessages.SUCCESSFUL_RESPONSE)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
