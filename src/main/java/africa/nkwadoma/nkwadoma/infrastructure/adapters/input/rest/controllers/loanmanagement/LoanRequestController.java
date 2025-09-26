package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.*;
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
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;

@RequestMapping(LOAN)
@RequiredArgsConstructor
@RestController
@Slf4j
public class LoanRequestController {
    private final LoanRequestUseCase loanRequestUseCase;
    private final LoanRequestRestMapper loanRequestRestMapper;

    @GetMapping("/loan-requests")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') " +
            "or hasRole('PORTFOLIO_MANAGER') or hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE') or hasRole('MEEDL_ADMIN')")
    public ResponseEntity<ApiResponse<?>> viewAllLoanRequests(
            @RequestParam(required = false) String organizationId,
            @RequestParam(required = false) String programId,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setPageNumber(pageNumber);
        loanRequest.setPageSize(pageSize);
        loanRequest.setProgramId(programId);
        loanRequest.setOrganizationId(organizationId);

        log.info("Request that got into controller organizationId == {} programID == {} pageNumber == {} pageSize == {}",
                loanRequest.getOrganizationId(),loanRequest.getProgramId(),
                loanRequest.getPageNumber(),loanRequest.getPageSize());

        Page<LoanRequest> loanRequests = loanRequestUseCase.viewAllLoanRequests(loanRequest, userId);
        log.info("Loan requests: {}", loanRequests.getContent());
        List<LoanRequestResponse> loanRequestResponses = loanRequests.stream().map(loanRequestRestMapper::toLoanRequestResponse).toList();
        log.info("Loan request responses: {}", loanRequestResponses);
        PaginatedResponse<LoanRequestResponse> paginatedResponse = new PaginatedResponse<>(
                loanRequestResponses, loanRequests.hasNext(),
                loanRequests.getTotalPages(),loanRequests.getTotalElements() , pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanRequestResponse>> apiResponse = ApiResponse.
                <PaginatedResponse<LoanRequestResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.LOAN_REQUESTS_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//    @GetMapping("{organizationId}/loan-requests")
//    @Operation(summary = "View all loan requests in an organization")
//    @ApiResponses(value = {
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan requests returned successfully",
//                    content =  @Content(mediaType = "application/json")),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid organization ID provided", content = @Content),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Organization not found", content = @Content)
//    })
//    public ResponseEntity<ApiResponse<?>> viewAllLoanRequests(
//            @Valid @PathVariable @NotBlank(message = "Organization ID is required")
//            @Parameter(description = "ID of the organization", required = true) String organizationId,
//            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
//            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) throws MeedlException {
//        LoanRequest loanRequest = new LoanRequest();
//        loanRequest.setOrganizationId(organizationId);
//        loanRequest.setPageNumber(pageNumber);
//        loanRequest.setPageSize(pageSize);
//        Page<LoanRequest> loanRequests = loanRequestUseCase.viewAllLoanRequests(loanRequest);
//        log.info("Loan requests returned from service layer: {}", loanRequests.getContent());
//        List<LoanRequestResponse> loanRequestResponses =
//                loanRequests.stream().map(loanRequestRestMapper::toLoanRequestResponse).toList();
//        log.info("Loan request response: {}", loanRequestResponses);
//        PaginatedResponse<LoanRequestResponse> paginatedResponse = new PaginatedResponse<>(
//                loanRequestResponses, loanRequests.hasNext(),
//                loanRequests.getTotalPages(),loanRequests.getTotalElements() , pageNumber, pageSize
//        );
//        ApiResponse<PaginatedResponse<LoanRequestResponse>> apiResponse = ApiResponse.
//                <PaginatedResponse<LoanRequestResponse>>builder()
//                .data(paginatedResponse)
//                .message(SuccessMessages.LOAN_REQUESTS_FOUND_SUCCESSFULLY)
//                .statusCode(HttpStatus.OK.name())
//                .build();
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

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
            (message = "Loan request ID is required") @Parameter(description = "ID of the loan request to be returned", required = true) String id,
                                                                 @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(id);
        String userId = meedlUser.getClaimAsString("sub");
        LoanRequest foundLoanRequest = loanRequestUseCase.viewLoanRequestById(loanRequest, userId);
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

    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @PostMapping("/loan-request/response")
    public ResponseEntity<ApiResponse<?>> respondToLoanRequest(@AuthenticationPrincipal Jwt meedlUser,
                                                               @Valid @RequestBody LoanRequestDto loanRequestDto)
            throws MeedlException {
        LoanRequest loanRequest = loanRequestRestMapper.toLoanRequest(loanRequestDto);
        loanRequest.setActorId(meedlUser.getClaimAsString("sub"));
        loanRequest = loanRequestUseCase.respondToLoanRequest(loanRequest);
        log.info("Loan request from service: {}", loanRequest);
        LoanRequestResponse loanRequestResponse = loanRequestRestMapper.toLoanRequestResponse(loanRequest);
        log.info("Mapped Loan request response: {}", loanRequestResponse);
        ApiResponse<LoanRequestResponse> apiResponse = ApiResponse.<LoanRequestResponse>builder()
                .data(loanRequestResponse)
                .message(loanRequestResponse.getStatus().equals(LoanRequestStatus.APPROVED) ?
                        SuccessMessages.LOAN_REQUEST_APPROVED_SUCCESSFULLY :
                        SuccessMessages.LOAN_REQUEST_DECLINED_SUCCESSFULLY
                )
                .statusCode(HttpStatus.OK.toString()).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    @PostMapping("/search/loan-request")
    public ResponseEntity<ApiResponse<?>> searchLoanRequest(@RequestParam(name = "name") String name,
                                                            @RequestParam(name = "organizationId", required = false) String organizationId,
                                                            @RequestParam(name = "programId", required = false) String programId,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {


        LoanRequest loanRequest = LoanRequest.builder().name(name).organizationId(organizationId).programId(programId)
                .pageNumber(pageNumber).pageSize(pageSize).build();
        log.info("request that got in name is == {}", loanRequest.getName());

        Page<LoanRequest> loanRequests = loanRequestUseCase.searchLoanRequest(loanRequest);
        List<LoanRequestResponse> loanRequestResponses = loanRequests.stream().map(loanRequestRestMapper::toLoanRequestResponse).toList();
        log.info("Loan request responses: {}", loanRequestResponses);
        PaginatedResponse<LoanRequestResponse> paginatedResponse = new PaginatedResponse<>(
                loanRequestResponses, loanRequests.hasNext(),
                loanRequests.getTotalPages(),loanRequests.getTotalElements() , pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanRequestResponse>> apiResponse = ApiResponse.
                <PaginatedResponse<LoanRequestResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.LOAN_REQUESTS_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
