package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeDeferRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeStatusRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBeneficiaryResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoaneeResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.LoaneeRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import jakarta.validation.Valid;
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

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.*;

@Slf4j
@RestController
@RequestMapping("loanee/")
@RequiredArgsConstructor
public class LoaneeController {

    private final LoaneeRestMapper loaneeRestMapper;
    private final LoaneeUseCase loaneeUseCase;

    @PostMapping("/invite")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> inviteLoanees(
                                                    @AuthenticationPrincipal Jwt meedlUser,
                                                    @RequestBody List<String> ids) {

        List<Loanee> loanees = loaneeRestMapper.map(ids, meedlUser.getClaimAsString("sub"));
        loanees = loaneeUseCase.inviteLoanees(loanees);
        List<LoaneeResponse> loaneeResponse =
                loaneeRestMapper.toLoaneeResponse(loanees);
        ApiResponse<List<LoaneeResponse>> apiResponse = ApiResponse.<List<LoaneeResponse>>builder()
                .data(loaneeResponse)
                .message(LOANEE_INVITED_TO_PLATFORM)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @PostMapping("cohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> addLoaneeToCohort(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestBody  @Valid LoaneeRequest loaneeRequest) throws MeedlException {
        Loanee loanee = loaneeRestMapper.toLoanee(loaneeRequest);
        loanee.getUserIdentity().setCreatedBy(meedlUser.getClaimAsString("sub"));
        loanee = loaneeUseCase.addLoaneeToCohort(loanee);
        LoaneeResponse loaneeResponse =
                loaneeRestMapper.toLoaneeResponse(loanee);
        ApiResponse<LoaneeResponse> apiResponse = ApiResponse.<LoaneeResponse>builder()
                .data(loaneeResponse)
                .message(LOANEE_ADDED_TO_COHORT)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @GetMapping("{loaneeId}")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')  or hasRole('PORTFOLIO_MANAGER')  or hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> viewLoaneeDetails(@PathVariable String loaneeId,
                                                            @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        Loanee loanee = loaneeUseCase.viewLoaneeDetails(loaneeId, userId);
        LoaneeResponse loaneeResponse =
                loaneeRestMapper.toLoaneeResponse(loanee);
        log.info("Loanee response: {}", loaneeResponse);
        ApiResponse<LoaneeResponse> apiResponse = ApiResponse.<LoaneeResponse>builder()
                .data(loaneeResponse)
                .message(LOANEE_VIEWED)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("cohorts/loanees")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllLoaneeInCohort(
            @RequestParam String cohortId,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "status" , required = false )LoaneeStatus loaneeStatus,
            @RequestParam(name= "loanStatus", required = false) LoanStatus loanStatus,
            @RequestParam(name = "uploadedStatus", required = false) UploadedStatus uploadedStatus
            ) throws MeedlException {
        Loanee loanee = Loanee.builder().cohortId(cohortId).loaneeStatus(loaneeStatus)
                .loanStatus(loanStatus).uploadedStatus(uploadedStatus).build();
        Page<Loanee> loanees = loaneeUseCase.viewAllLoaneeInCohort(loanee, pageSize, pageNumber);
        List<LoaneeResponse> loaneeResponses = loanees.stream()
                .map(loaneeRestMapper::toLoaneeResponse).toList();
        PaginatedResponse<LoaneeResponse> paginatedResponse = new PaginatedResponse<>(
                loaneeResponses, loanees.hasNext(),
                loanees.getTotalPages(),loanees.getTotalElements() , pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoaneeResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoaneeResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.toString())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("cohorts/search/loanees")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchForLoaneeInCohort(@RequestParam("loaneeName")String loaneeName,
                                                                  @RequestParam("cohortId")String cohortId,
                                                                  @RequestParam(name = "status" , required = false ) LoaneeStatus status,
                                                                  @RequestParam(name = "uploadedStatus", required = false) UploadedStatus uploadedStatus,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber)throws MeedlException {
        Loanee loanee = Loanee.builder().cohortId(cohortId).loaneeStatus(status)
                .loaneeName(loaneeName).uploadedStatus(uploadedStatus).build();
       Page<Loanee> loanees = loaneeUseCase.searchForLoaneeInCohort(loanee,pageSize,pageNumber);
       List<LoaneeResponse> loaneeResponse = loanees.stream()
               .map(loaneeRestMapper::toLoaneeResponse).toList();
       PaginatedResponse<LoaneeResponse> paginatedResponse = new PaginatedResponse<>(
               loaneeResponse,loanees.hasNext(),
               loanees.getTotalPages(), loanees.getTotalElements() , pageNumber, pageSize
       );
       ApiResponse<PaginatedResponse<LoaneeResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoaneeResponse>>builder()
               .data(paginatedResponse)
               .message(LOANEE_RETRIEVED)
               .statusCode(HttpStatus.OK.toString())
               .build();
       return new ResponseEntity<>(apiResponse,HttpStatus.OK);

    }

    @GetMapping("loanProduct/loanees/{loanProductId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllLoanBeneficiaryFromLoanProduct(
            @PathVariable String loanProductId,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber

    )throws MeedlException {

        Page<Loanee> loanees =
                loaneeUseCase.viewAllLoaneeThatBenefitedFromLoanProduct(loanProductId,pageSize,pageNumber);
        List<LoanBeneficiaryResponse> loaneeResponses = loanees.stream()
                .map(loaneeRestMapper::toLoanBeneficiaryResponse).toList();
        PaginatedResponse<LoanBeneficiaryResponse> paginatedResponse = new PaginatedResponse<>(
                loaneeResponses, loanees.hasNext(),
                loanees.getTotalPages(),loanees.getTotalElements() , pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanBeneficiaryResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanBeneficiaryResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.toString())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("loan-product/search/loanees/{loanProductId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchLoanBeneficiaryFromLoanProduct(
            @PathVariable String loanProductId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber

    )throws MeedlException {

        Page<Loanee> loanees =
                loaneeUseCase.searchLoaneeThatBenefitedFromLoanProduct(loanProductId,name,pageSize,pageNumber);
        List<LoanBeneficiaryResponse> loaneeResponses = loanees.stream()
                .map(loaneeRestMapper::toLoanBeneficiaryResponse).toList();
        PaginatedResponse<LoanBeneficiaryResponse> paginatedResponse = new PaginatedResponse<>(
                loaneeResponses, loanees.hasNext(),
                loanees.getTotalPages(),loanees.getTotalElements() , pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanBeneficiaryResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanBeneficiaryResponse>>builder()
                .data(paginatedResponse)
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.toString())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("defer/loan")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> deferLoan(@AuthenticationPrincipal Jwt meedlUser,
                                                       @RequestParam String loanId,
                                                       @RequestParam String reasonForDeferral) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        String response = loaneeUseCase.deferLoan(userId, loanId, reasonForDeferral);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @PutMapping("resume/program")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> resumeProgram(@AuthenticationPrincipal Jwt meedlUser,
                                                       @RequestParam String loanId,
                                                       @RequestParam String cohortId) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        String response = loaneeUseCase.resumeProgram(loanId, cohortId, userId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);

    }


    @PostMapping("defer")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> indicateDeferLoanee(@AuthenticationPrincipal Jwt meedlUser,
                                                              @RequestBody LoaneeDeferRequest loaneeDeferRequest) throws MeedlException {
        String response = loaneeUseCase.indicateDeferredLoanee(meedlUser.getClaimAsString("sub"), loaneeDeferRequest.getLoaneeId());
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(LOANEE_DEFERRED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @PostMapping("indicate/dropout")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> indicateDropOutLoanee(@AuthenticationPrincipal Jwt meedlUser,
                                                                @RequestParam String loanId) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        String response = loaneeUseCase.indicateDropOutLoanee(userId, loanId);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(LOANEE_DROPOUT)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @PostMapping("self/dropout")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> dropOutFromCohort(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestParam String loanId,
                                                            @RequestParam String reasonForDropout
                                                            ) throws MeedlException{

        String userId = meedlUser.getClaimAsString("sub");
        String response = loaneeUseCase.dropOutFromCohort(userId, loanId, reasonForDropout);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(LOANEE_DROPOUT_REQUEST_SENT)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @PostMapping("status")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> archiveOrUnArchiveLoanee(@AuthenticationPrincipal Jwt meedlUser,
                                                                   @RequestBody LoaneeStatusRequest loaneeStatusRequest) throws MeedlException{
        String response = loaneeUseCase.archiveOrUnArchiveByIds(meedlUser.getClaimAsString("sub"),
                loaneeStatusRequest.getLoaneeIds(),loaneeStatusRequest.getLoaneeStatus());
        ApiResponse<String > apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(LOANEE_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
}
