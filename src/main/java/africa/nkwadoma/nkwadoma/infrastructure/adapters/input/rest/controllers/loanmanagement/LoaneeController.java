package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.EmploymentStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.EditLoaneeDetailRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.UpdateLoaneeProfileRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeDeferRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeStatusRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.EditLoaneeDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.EmploymentStatusResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.CohortLoaneeResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBeneficiaryResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoaneeLoanAggregateResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeLoanDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeProfileResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeResponse;
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

    @PostMapping("/invite/{cohortId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN')")
    public ResponseEntity<ApiResponse<?>> inviteLoanees(
                                                    @AuthenticationPrincipal Jwt meedlUser,
                                                    @PathVariable String cohortId,
                                                    @RequestBody List<String> ids) {

        log.info("Gotten to the controller to invite loanee(s) to the platform...");
        List<Loanee> loanees = loaneeRestMapper.map(ids, meedlUser.getClaimAsString("sub"));
        loanees = loaneeUseCase.inviteLoanees(loanees, cohortId);
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
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
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

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')  or hasRole('PORTFOLIO_MANAGER')  or hasRole('LOANEE') or hasRole('ORGANIZATION_SUPER_ADMIN')" +
            "or hasRole('MEEDL_SUPER_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('MEEDL_ADMIN')")
    public ResponseEntity<ApiResponse<?>> viewLoaneeDetails(@RequestParam(required = false) String loaneeId,
                                                            @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        Loanee loanee = loaneeUseCase.viewLoaneeDetails(loaneeId, userId);
        LoaneeProfileResponse loaneeResponse =
                loaneeRestMapper.toLoaneeProfileResponse(loanee);
        log.info("Loanee response: {}", loaneeResponse);
        ApiResponse<LoaneeProfileResponse> apiResponse = ApiResponse.<LoaneeProfileResponse>builder()
                .data(loaneeResponse)
                .message(LOANEE_VIEWED )
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("cohorts/loanees")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
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
        Page<CohortLoanee> loanees = loaneeUseCase.viewAllLoaneeInCohort(loanee, pageSize, pageNumber);
        List<LoaneeResponse> loaneeResponses = loanees.stream()
                .map(loaneeRestMapper::mapToLoaneeResponse).toList();
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
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE') ")
    public ResponseEntity<ApiResponse<?>> searchForLoaneeInCohort(@RequestParam("loaneeName")String loaneeName,
                                                                  @RequestParam("cohortId")String cohortId,
                                                                  @RequestParam(name = "status" , required = false ) LoaneeStatus status,
                                                                  @RequestParam(name = "uploadedStatus", required = false) UploadedStatus uploadedStatus,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber)throws MeedlException {
        Loanee loanee = Loanee.builder().cohortId(cohortId).loaneeStatus(status)
                .loaneeName(loaneeName).uploadedStatus(uploadedStatus).build();
       Page<CohortLoanee> loanees = loaneeUseCase.searchForLoaneeInCohort(loanee,pageSize,pageNumber);
        List<LoaneeResponse> loaneeResponse = loanees.stream()
               .map(loaneeRestMapper::mapToLoaneeResponse).toList();
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

    @GetMapping("cohorts/loanee")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('ORGANIZATION_SUPER_ADMIN')or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewLoaneeInCohort(@RequestParam("cohortId")String cohortId,
                                                             @RequestParam("loaneeId") String loaneeId) throws MeedlException {
        log.info("request that came in cohortID == {} , loaneeId == {}", cohortId, loaneeId);
        CohortLoanee cohortLoanee = loaneeUseCase.viewLoaneeDetailInCohort(cohortId,loaneeId);
        CohortLoaneeResponse cohortLoaneeResponse = loaneeRestMapper.toCohortLoaneeResponse(cohortLoanee);

        ApiResponse<CohortLoaneeResponse> apiResponse = ApiResponse.<CohortLoaneeResponse>builder()
                .data(cohortLoaneeResponse)
                .message(LOANEE_DEAILS_IN_A_COHORT)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("loan/detail")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> viewLoaneeLoanDetail(@RequestParam("cohortLoaneeId")String cohortLoaneeId) throws MeedlException {
        log.info("request that came in  for view loanee loan detail with cohort loanee id {}", cohortLoaneeId);
        LoaneeLoanDetail loaneeLoanDetail = loaneeUseCase.viewLoaneeLoanDetail(cohortLoaneeId);
        LoaneeLoanDetailResponse loanDetailResponse = loaneeRestMapper.toLoaneeLoanDetail(loaneeLoanDetail);

        ApiResponse<LoaneeLoanDetailResponse> apiResponse = ApiResponse.<LoaneeLoanDetailResponse>builder()
                .data(loanDetailResponse)
                .message(LOANEE_LOAN_DETAIL_VIEW)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @GetMapping("loanProduct/loanees/{loanProductId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANGER_ASSOCIATE') ")
    public ResponseEntity<ApiResponse<?>> viewAllLoanBeneficiaryFromLoanProduct(
            @PathVariable String loanProductId,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber

    )throws MeedlException {

        Page<CohortLoanee> loanees =
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE') ")
    public ResponseEntity<ApiResponse<?>> searchLoanBeneficiaryFromLoanProduct(
            @PathVariable String loanProductId,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber

    )throws MeedlException {

        Page<CohortLoanee> loanees =
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
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> archiveOrUnArchiveLoanee(@RequestBody LoaneeStatusRequest loaneeStatusRequest) throws MeedlException{
        String response = loaneeUseCase.archiveOrUnArchiveByIds(loaneeStatusRequest.getCohortId(),
                loaneeStatusRequest.getLoaneeIds(),loaneeStatusRequest.getLoaneeStatus());
        ApiResponse<String > apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(LOANEE_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @GetMapping("all")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
                  "or hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewAllLoanees( @AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException{
        Page<LoaneeLoanAggregate> loaneeLoanAggregate  = loaneeUseCase.viewAllLoanee(meedlUser.getClaimAsString("sub"),pageSize,pageNumber);
        List<LoaneeLoanAggregateResponse> loaneeLoanAggregateResponses =
                loaneeLoanAggregate.map(loaneeRestMapper::toLoaneeLoanAggregateResponse).stream().toList();

        PaginatedResponse<LoaneeLoanAggregateResponse> loanAggregateResponsePaginatedResponse =
                new PaginatedResponse<>(loaneeLoanAggregateResponses,loaneeLoanAggregate.hasNext(),loaneeLoanAggregate.getTotalPages(),
                        loaneeLoanAggregate.getTotalElements(),pageNumber,pageSize);
        ApiResponse<PaginatedResponse<LoaneeLoanAggregateResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoaneeLoanAggregateResponse>>builder()
                .data(loanAggregateResponsePaginatedResponse)
                .message(LOANEE_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("all/search")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
                  "or hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> searchAllLoanees(@AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestParam(name = "name") String name,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException{
        Loanee loanee = Loanee.builder().loaneeName(name).id(meedlUser.getClaimAsString("sub")).build();

        Page<LoaneeLoanAggregate> loaneeLoanAggregate  = loaneeUseCase.searchLoanAggregate(loanee,pageSize,pageNumber);
        List<LoaneeLoanAggregateResponse> loaneeLoanAggregateResponses =
                loaneeLoanAggregate.map(loaneeRestMapper::toLoaneeLoanAggregateResponse).stream().toList();

        PaginatedResponse<LoaneeLoanAggregateResponse> loanAggregateResponsePaginatedResponse =
                new PaginatedResponse<>(loaneeLoanAggregateResponses,loaneeLoanAggregate.hasNext(),loaneeLoanAggregate.getTotalPages(),
                        loaneeLoanAggregate.getTotalElements(),pageNumber,pageSize);
        ApiResponse<PaginatedResponse<LoaneeLoanAggregateResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoaneeLoanAggregateResponse>>builder()
                .data(loanAggregateResponsePaginatedResponse)
                .message(LOANEE_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping("cohort/employment/status")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> setEmploymentStatus(@RequestParam(name = "employmentStatus") EmploymentStatus employmentStatus,
                                                              @RequestParam(name = "cohortId") String cohortId,
                                                              @RequestParam(name = "loaneeId") String loaneeId) throws MeedlException{

        CohortLoanee cohortLoanee = loaneeUseCase.setEmploymentStatus(employmentStatus,cohortId,loaneeId);
        EmploymentStatusResponse employmentStatusResponse = loaneeRestMapper.mapToEmploymentStatusResponse(cohortLoanee);
        ApiResponse<EmploymentStatusResponse> apiResponse = ApiResponse.<EmploymentStatusResponse>builder()
                .data(employmentStatusResponse)
                .message(EMPLOYMENT_STATUS_UPDATED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping("cohort/training/performance")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> updateTrainingPerformance(@RequestParam(name = "trainingPerformance") String trainingPerformance,
                                                                    @RequestParam(name = "cohortId") String cohortId,
                                                                    @RequestParam(name = "loaneeId") String loaneeId) throws MeedlException {

       String trainingPerformanceLink =  loaneeUseCase.updateTrainingPerformance(trainingPerformance,cohortId,loaneeId);
       ApiResponse<String> apiResponse = ApiResponse.<String>builder()
               .data(trainingPerformanceLink)
               .message(TRAINING_PERFORMANCE_UPDATED)
               .statusCode(HttpStatus.OK.toString())
               .build();
       return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



    @PostMapping("edit/detail")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> editLoaneeDetail(@RequestBody @Valid EditLoaneeDetailRequest editLoaneeDetailRequest) throws MeedlException {

        Loanee loanee = loaneeRestMapper.mapToCohortLoanee(editLoaneeDetailRequest);
        loanee = loaneeUseCase.editLoaneeDetail(loanee);
        EditLoaneeDetailResponse editLoaneeDetailResponse = loaneeRestMapper.maptToEditLoaneeDetailResponse(loanee);
        ApiResponse<EditLoaneeDetailResponse> apiResponse = ApiResponse.<EditLoaneeDetailResponse>builder()
                .data(editLoaneeDetailResponse)
                .message(LOANEE_DETAIL_EDITED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("update/profile")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> updateLoaneeProfile(@RequestBody @Valid UpdateLoaneeProfileRequest updateLoaneeProfileRequest,
                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        Loanee loanee = loaneeRestMapper.mapUpdateLoaneeProfileToLoanee(updateLoaneeProfileRequest);
        String response = loaneeUseCase.updateLoaneeProfile(loanee,meedlUser.getClaimAsString("sub"));
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(LOANEE_PROFILE_UPDATED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
