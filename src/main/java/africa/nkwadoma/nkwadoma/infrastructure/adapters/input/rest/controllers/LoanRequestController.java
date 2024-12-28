package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
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
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("{organizationId}/loan-requests")
    @Operation(summary = "View all loan requests in an organization")
    public ResponseEntity<ApiResponse<?>> viewAllLoanRequests(@Valid
            @PathVariable @NotBlank(message = "Organization ID is required") String organizationId,
                                                              @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) throws MeedlException {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setOrganizationId(organizationId);
        loanRequest.setPageNumber(pageNumber);
        loanRequest.setPageSize(pageSize);
        Page<LoanRequest> loanRequests = loanRequestUseCase.viewAllLoanRequests(loanRequest);
        log.info("Loan requests returned from service layer: {}", loanRequests.getContent());
        List<LoanRequestResponse> loanRequestResponses =
                loanRequests.stream().map(loanRequestRestMapper::toLoanRequestResponse).toList();
        log.info("Loan request response: {}", loanRequestResponses);
        PaginatedResponse<LoanRequestResponse> paginatedResponse = new PaginatedResponse<>(
                loanRequestResponses, loanRequests.hasNext(),
                loanRequests.getTotalPages(), pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanRequestResponse>> apiResponse = ApiResponse.
                <PaginatedResponse<LoanRequestResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.LOAN_REQUESTS_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "View a loan request by its id")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found the loan request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanRequestResponse.class)) }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan request not found",
                    content = @Content) })
    @GetMapping("/loan-requests/{id}")
    public ResponseEntity<ApiResponse<?>> viewLoanRequestDetails(@Valid @PathVariable @NotBlank
            (message = "Loan request ID is required") @Parameter(description = "ID of the loan request to be returned", required = true) String id) throws MeedlException {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(id);
        LoanRequest foundLoanRequest = loanRequestUseCase.viewLoanRequestById(loanRequest);
        log.info("Loan request body: {}", foundLoanRequest);
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
